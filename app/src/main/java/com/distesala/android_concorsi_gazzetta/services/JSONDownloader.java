package com.distesala.android_concorsi_gazzetta.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.adapter.GsonContestAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.database.GazzetteDataSource;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.execptions.HttpErrorException;
import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Questa classe inizia ad avere un pò troppe responsabilità.
 * O si rinomina con qualcosa del tipo Worker*
 * oppure si creano più classi IntentService
 */


public class JSONDownloader extends IntentService
{
    private static final String URL_WS = "http://marsala.ddns.net:8080/gazzetteWithContests";
    private static final String LATEST_GAZZETTA = "http://marsala.ddns.net:8080/latestGazzetta";
    public static final String DOWNLOAD_GAZZETTA = "com.distesala.android_concorsi_gazzetta.services.action.DownloadGazzetta";
    public static final String GET_CONTEST_FOR_GAZZETTA = "com.distesala.android_concorsi_gazzetta.services.action.GetContestForGazzetta";

    public static final String CURSOR_GAZZETTA = "cursor_gazzetta";
    public static final String CURSOR_CONTESTS = "cursor_contests";

    private GazzetteDataSource dataSource; //lo devo mettere come campo per forza di cose

    public JSONDownloader()
    {
        super("JSONDownloader");

    }

    /*public static void startDownloadGazzetta(Context context)
    {
        Intent intent = new Intent(context, JSONDownloader.class);
        intent.setAction(DOWNLOAD_GAZZETTA);
        context.startService(intent);
    }*/

    private void insertGazzetteWithContests(Gazzetta[] gazzette)
    {
        ContentValues gazzetteContentValues[] = new ContentValues[gazzette.length];
        Log.i("provider array size", String.valueOf(gazzetteContentValues.length));
        for(int i = 0; i < gazzette.length; i++)
        {
            //gazzetteContentValues[i].put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA, gazzette[i].getIdGazzetta());
            gazzetteContentValues[i].put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, gazzette[i].getNumberOfPublication());
            gazzetteContentValues[i].put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  gazzette[i].getDateOfPublication());

            int numberOfContests = gazzette[i].getConcorsi().size();

            ContentValues contestsContentValues[] = new ContentValues[numberOfContests];

            for (int j = 0; j < gazzette[i].getConcorsi().size(); j++)
            {
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO, gazzette[i].getConcorsi().get(j).getCodiceRedazionale());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION, gazzette[i].getNumberOfPublication());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO, gazzette[i].getConcorsi().get(j).getTitoloConcorso());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE, gazzette[i].getConcorsi().get(j).getEmettitore());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_AREA, gazzette[i].getConcorsi().get(j).getAreaDiInteresse());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA, gazzette[i].getConcorsi().get(j).getTipologia());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA, gazzette[i].getConcorsi().get(j).getScadenza());
                contestsContentValues[j].put(GazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI, gazzette[i].getConcorsi().get(j).getAreaDiInteresse());
            }

            //insert contentvalues array of contests for a single gazzetta
            getContentResolver().bulkInsert(ConcorsiGazzettaContentProvider.CONTESTS_URI, contestsContentValues);

        }

        //insert array of gazzette

        getContentResolver().bulkInsert(ConcorsiGazzettaContentProvider.GAZZETTE_URI, gazzetteContentValues);

    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();

            if (DOWNLOAD_GAZZETTA.equals(action))
            {
                ResultReceiver rec = intent.getParcelableExtra("receiverTag");
                try
                {
                    String json = downloadGazzette(new URL(URL_WS));
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray gazzetteJsonArray = jsonObject.getJSONArray("gazzette");

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    //custom adapter for deserialization
                    gsonBuilder.registerTypeAdapter(Concorso.class, new GsonContestAdapter());
                    Gson gson = gsonBuilder.create();

                    Gazzetta[] gazzette = gson.fromJson(gazzetteJsonArray.toString(), Gazzetta[].class);
                    Log.i("provider date of pub", gazzette[0].getDateOfPublication());
                    insertGazzetteWithContests(gazzette);

                    Bundle b = new Bundle();
                    b.putSerializable(CURSOR_GAZZETTA, null);
                    rec.send(Activity.RESULT_OK, b);
                    Log.i("provider", "service finito");
                }

                catch (Exception e)
                {
                    Log.i("IntentService", "Errore");
                    e.printStackTrace();
                    rec.send(Activity.RESULT_CANCELED, null);
                }
            }
        }
    }


    //URGE REFACTORONE
    /*@Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();

            dataSource = new GazzetteDataSource(getApplicationContext());

            if (DOWNLOAD_GAZZETTA.equals(action))
            {
                ResultReceiver rec = intent.getParcelableExtra("receiverTag");
                try
                {
                    //apro il database
                    dataSource.open();

                    if (!updatedGazzette())
                    {

                        String json = downloadGazzette(new URL(URL_WS));
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray gazzetteJsonArray = jsonObject.getJSONArray("gazzette");

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        //custom adapter for deserialization
                        gsonBuilder.registerTypeAdapter(Concorso.class, new GsonContestAdapter());
                        Gson gson = gsonBuilder.create();

                        Gazzetta[] gazzette = gson.fromJson(gazzetteJsonArray.toString(), Gazzetta[].class);

                        dataSource.insertGazzette(gazzette);
                    }
                    else
                    {
                        Log.i("IntentService", "Archivio aggiornato!");
                    }

                    //creo il cursor da mandare all'activity

                    Cursor cursor = dataSource.getGazzetteCursor();
                    CursorEnvelope cursorEnvelope = new CursorEnvelope(cursor);
                    Log.i("IntentService[GAZZETTE]", String.valueOf(cursor.getCount()));
                    Bundle b = new Bundle();
                    b.putSerializable(CURSOR_GAZZETTA, cursorEnvelope);
                    rec.send(Activity.RESULT_OK, b);

                    //il lavoro del service è finito, posso chiudere il database
                    dataSource.close();

                }
                catch (Exception e)
                {
                    Log.i("IntentService", "Errore");
                    e.printStackTrace();
                    rec.send(Activity.RESULT_CANCELED, null);
                    dataSource.close();
                }
            }

            else if(action.equals(GET_CONTEST_FOR_GAZZETTA))
            {
                ResultReceiver rec = intent.getParcelableExtra("receiverTag");
                String numberOfPublication = intent.getStringExtra("gazzettaNumberOfPublication");

                dataSource.open();
                Cursor cursor = dataSource.getContestsForGazzetta(numberOfPublication);
                CursorEnvelope cursorEnvelope = new CursorEnvelope(cursor);
                Log.i("IntentService [CONTEST]", String.valueOf(cursor.getCount()));
                Bundle b = new Bundle();
                b.putSerializable(CURSOR_CONTESTS, cursorEnvelope);
                rec.send(Activity.RESULT_OK, b);

                dataSource.close();
            }
        }
    }*/

    private boolean updatedGazzette()
    {
        try
        {
            String json = downloadGazzette(new URL(LATEST_GAZZETTA));
            Gson gson = new Gson();
            Gazzetta latest = gson.fromJson(json, Gazzetta.class);
            return dataSource.gazzettaExists(latest);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private String downloadGazzette(URL url) throws Exception
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
