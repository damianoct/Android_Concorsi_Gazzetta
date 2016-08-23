package com.distesala.android_concorsi_gazzetta.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.database.GazzetteDataSource;
import com.distesala.android_concorsi_gazzetta.execptions.HttpErrorException;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.google.gson.Gson;

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

public class JSONDownloader extends IntentService
{
    private static final String URL_WS = "http://marsala.ddns.net:8080/gazzetteWithContests";
    private static final String LATEST_GAZZETTA = "http://marsala.ddns.net:8080/latestGazzetta";
    public static final String DOWNLOAD_GAZZETTA = "com.distesala.android_concorsi_gazzetta.services.action.DownloadGazzetta";


    public static final String CURSOR_GAZZETTA = "cursor_gazzetta";

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
                    dataSource = new GazzetteDataSource(getApplicationContext());
                    //apro il database
                    dataSource.open();

                    if (!updatedGazzette())
                    {

                        String json = downloadGazzette(new URL(URL_WS));
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray gazzetteJsonArray = jsonObject.getJSONArray("gazzette");

                        Gson gson = new Gson();
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
                    Log.i("IntentService", String.valueOf(cursor.getCount()));
                    Bundle b = new Bundle();
                    b.putSerializable(CURSOR_GAZZETTA, cursorEnvelope);
                    rec.send(Activity.RESULT_OK, b);

                    //il lavoro del service è finito, posso chiudere il database
                    dataSource.close();

                }
                catch (Exception e)
                {
                    Log.i("IntentService", "Errore");
                    rec.send(Activity.RESULT_CANCELED, null);
                    dataSource.close();
                }
            }
        }
    }

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
