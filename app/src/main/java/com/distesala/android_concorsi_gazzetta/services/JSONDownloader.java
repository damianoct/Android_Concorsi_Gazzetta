package com.distesala.android_concorsi_gazzetta.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JSONDownloader extends IntentService
{
    private static final String URL_DAMIANODDS = "http://dds.zapto.org:8080/gazzette";

    private static final String DOWNLOAD_GAZZETTA = "com.distesala.android_concorsi_gazzetta.services.action.DownloadGazzetta";
    private static final String RESULT_JSON = "com.distesala.android_concorsi_gazzetta.services.action.ResultJSON";

    public JSONDownloader()
    {
        super("JSONDownloader");
    }

    public static void startDownloadGazzetta(Context context)
    {
        Intent intent = new Intent(context, JSONDownloader.class);
        intent.setAction(DOWNLOAD_GAZZETTA);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (DOWNLOAD_GAZZETTA.equals(action))
            {

                try
                {
                    String json = downloadGazzette();
                    //Intent localIntent = new Intent();
                    Log.i("JSON", json);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private String downloadGazzette() throws Exception
    {
        String json = null;

        try
        {
            URL url = new URL(URL_DAMIANODDS);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200)
            {
                json =  convertInputStreamToString(urlConnection.getInputStream());
            }
            else
            {
                //TODO: crea l'eccezione che ti piace per la connessione rifiutata
                throw new Exception("EREOOOOTOOOOO");
            }

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


    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (inputStream != null)
        {
            inputStream.close();
        }

        return result;
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2)
    {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
