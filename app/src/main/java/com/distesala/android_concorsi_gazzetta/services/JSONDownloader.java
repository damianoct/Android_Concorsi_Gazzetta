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
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
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
    private static final String URL_WS = "http://marsala.ddns.net:8080/gazzetteWithContests";
    private static final String URL = "http://marsala.ddns.net:8080/";
    private static final String LATEST_GAZZETTA = "http://marsala.ddns.net:8080/latestGazzetta";
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
            ContentValues gazzettaValues = GazzetteSQLiteHelper.createContentValuesForGazzetta(g);

            List<ContentValues> contestsValueList = new ArrayList<>();

            for(Concorso c: g.getConcorsi())
                contestsValueList.add(GazzetteSQLiteHelper.createContentValuesForContest(c));

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
                        Log.i("INTENT SERVICE", "Sto aggionando il Database");

                        try
                        {

                            String json = downloadJSON(new URL(URL_WS));
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
                            Log.i("IntentService", "Errore");
                            e.printStackTrace();
                            rec.send(Activity.RESULT_CANCELED, null);
                        }
                    }
                }
                else
                {
                    Log.i("IntentService", "Manca la Conneccione");
                    rec.send(Connectivity.CONNECTION_LOCKED, null);
                }

                rec.send(Activity.RESULT_OK, null);
                Log.i("provider", "service finito");
            }
            else if (DOWNLOAD_CONTEST.equals(action))
            {
                Log.i("melinta", "PATTITO");

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
                    String json = downloadJSON(new URL(link));
                    JSONObject jsonObject = new JSONObject(json);

                    Gson gson = new Gson();
                    ConcorsoContent content = gson.fromJson(jsonObject.toString(), ConcorsoContent.class);

                    Bundle b = new Bundle(1);
                    b.putStringArrayList("articles", new ArrayList<>(content.articoliBando));
                    rec.send(Activity.RESULT_OK, b);

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
            String json = downloadJSON(new URL(LATEST_GAZZETTA));
            Gson gson = new Gson();


            Gazzetta latest = gson.fromJson(json, Gazzetta.class);
            Cursor cursor = getContentResolver().query(
                    ConcorsiGazzettaContentProvider.GAZZETTE_URI,
                    null,
                    GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " =?",
                    new String[]{latest.getNumberOfPublication()},
                    null);

            Log.d("DEBUG", String.valueOf(cursor.getCount()));
            Log.d("DDEBUG", latest.getNumberOfPublication());
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
            Log.i("provider", "download iniziato");
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

        catch (UnknownHostException | HttpErrorException e)
        {
            Log.i("IntentService", "UnknownHost Exception or HttpErrorException");

        }

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
