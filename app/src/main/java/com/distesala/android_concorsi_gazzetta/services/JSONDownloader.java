package com.distesala.android_concorsi_gazzetta.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.GsonGazzettaAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.execptions.HttpErrorException;
import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.ConcorsoContent;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.distesala.android_concorsi_gazzetta.networking.Connectivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class JSONDownloader extends IntentService
{
    private static final String URL = "http://35.162.163.67:8080/";
    private static final String URL_HOME = URL + "gazzetteWithContests";
    private static final String LATEST_GAZZETTA_URL = URL + "latestGazzetta";
    private static final String GAZZETTA_URL = "http://www.gazzettaufficiale.it/eli/id/";
    public static final String DOWNLOAD_GAZZETTA = "DownloadGazzetta";
    public static final String DOWNLOAD_CONTEST = "DownloadContest";

    public JSONDownloader()
    {
        super("JSONDownloader");
    }

    private void insertGazzetteWithContests(Gazzetta[] gazzette)
    {
        List<ContentValues> gazzetteValueList = new ArrayList<>();

        for(Gazzetta g: gazzette)
        {
            ContentValues gazzettaValues = ConcorsiGazzetteSQLiteHelper.createContentValuesForGazzetta(g);

            List<ContentValues> contestsValueList = new ArrayList<>();

            for(Concorso c: g.getConcorsi())
                contestsValueList.add(ConcorsiGazzetteSQLiteHelper.createContentValuesForContest(c));

            ContentValues[] contestsContentValues = contestsValueList.toArray(new ContentValues[0]);
            getContentResolver().bulkInsert(ConcorsiGazzettaContentProvider.CONTESTS_URI, contestsContentValues);

            gazzetteValueList.add(gazzettaValues);
        }

        ContentValues[] gazzetteContentValues = gazzetteValueList.toArray(new ContentValues[0]);
        getContentResolver().bulkInsert(ConcorsiGazzettaContentProvider.GAZZETTE_URI, gazzetteContentValues);

    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();

            //every intent action has a receiver.
            ResultReceiver rec = intent.getParcelableExtra("receiverTag");

            if (DOWNLOAD_GAZZETTA.equals(action))
            {
                if(canConnect())
                {
                    if (!updatedGazzette())
                    {
                        try
                        {
                            String json = downloadJSON(new URL(URL_HOME));
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray gazzetteJsonArray = jsonObject.getJSONArray("gazzette");

                            GsonBuilder gsonBuilder = new GsonBuilder();

                            gsonBuilder.registerTypeAdapter(Gazzetta.class, new GsonGazzettaAdapter());
                            Gson gson = gsonBuilder.create();

                            Gazzetta[] gazzette = gson.fromJson(gazzetteJsonArray.toString(), Gazzetta[].class);

                            insertGazzetteWithContests(gazzette);
                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                            rec.send(Activity.RESULT_CANCELED, null);
                        }
                    }
                }
                else
                {
                    rec.send(Connectivity.CONNECTION_LOCKED, null);
                }

                rec.send(Activity.RESULT_OK, null);
            }
            else if (DOWNLOAD_CONTEST.equals(action))
            {
                String dateOfPublication = intent.getStringExtra(Gazzetta.DATE_OF_PUBLICATION);
                String contestID = intent.getStringExtra(Concorso.CONTEST_ID);

                String day = dateOfPublication.substring(8,10);
                String month = dateOfPublication.substring(5,7);
                String year = dateOfPublication.substring(0,4);

                String link = URL + "concorso?giorno="
                                    + day + "&mese="
                                    + month + "&anno="
                                    + year + "&codiceRedazionale=" + contestID;
                try
                {
                    if(canConnect())
                    {
                        String json = downloadJSON(new URL(link));
                        JSONObject jsonObject = new JSONObject(json);

                        Gson gson = new Gson();
                        ConcorsoContent content = gson.fromJson(jsonObject.toString(), ConcorsoContent.class);

                        Bundle b = new Bundle(1);
                        b.putStringArrayList("articles", new ArrayList<>(content.articoliBando));

                        String gazzettaURL = GAZZETTA_URL
                                + year + "/"
                                + month + "/"
                                + day + "/"
                                + contestID + "/s4";

                        b.putString("url", gazzettaURL);
                        rec.send(Activity.RESULT_OK, b);
                    }
                    else
                    {
                        rec.send(Connectivity.CONNECTION_LOCKED, null);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean canConnect()
    {
        Context c = getApplicationContext();
        String cellularKey = getString(R.string.key_utilizzo_rete_dati);
        boolean cellularEnabled = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(cellularKey, true);

        return Connectivity.isConnectedWifi(c) ||
                (Connectivity.isConnectedMobile(c) && cellularEnabled);
    }

    private boolean updatedGazzette()
    {
        try
        {
            String json = downloadJSON(new URL(LATEST_GAZZETTA_URL));
            Gson gson = new Gson();


            Gazzetta latest = gson.fromJson(json, Gazzetta.class);

            Cursor cursor = getContentResolver().query(
                    ConcorsiGazzettaContentProvider.GAZZETTE_URI,
                    null,
                    ConcorsiGazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " =?",
                    new String[]{latest.getNumberOfPublication()},
                    null);

            return cursor.moveToNext();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private String downloadJSON(URL url) throws Exception
    {
        String json = null;

        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200)
            {
                json =  convertInputStreamToString(urlConnection.getInputStream());
            }
            else
            {
                throw new HttpErrorException("Error to connect to: " + url);
            }
        }

        catch (UnknownHostException | HttpErrorException e) {}

        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return json;
    }


    private String convertInputStreamToString(InputStream inputStream) throws IOException
    {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null)
        {
            result += line;
        }

        if (inputStream != null)
        {
            inputStream.close();
        }
        return result;
    }
}
