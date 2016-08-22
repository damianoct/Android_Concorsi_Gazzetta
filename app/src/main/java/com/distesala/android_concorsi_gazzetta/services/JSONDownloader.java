package com.distesala.android_concorsi_gazzetta.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.database.GazzetteDataSource;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JSONDownloader extends IntentService
{
    private static final String URL_WS = "http://marsala.ddns.net:8080/gazzetteWithContests";

    public static final String DOWNLOAD_GAZZETTA = "com.distesala.android_concorsi_gazzetta.services.action.DownloadGazzetta";

    public static final String CURSOR_GAZZETTA = "cursor_gazzetta";

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
                    String json = downloadGazzette();
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray gazzetteJsonArray = jsonObject.getJSONArray("gazzette");

                    Gson gson = new Gson();
                    Gazzetta[] gazzette = gson.fromJson(gazzetteJsonArray.toString(), Gazzetta[].class);

                    //GSON Finito

                    GazzetteDataSource dataSource = new GazzetteDataSource(getApplicationContext());
                    dataSource.open();

                    dataSource.insertGazzetta(gazzette[0]);
                    /*
                    List<Gazzetta> gazzetteList = dataSource.getAllGazzette();
                    */
                    Cursor cursor = dataSource.getGazzetteCursor();
                    CursorEnvelope cursorEnvelope = new CursorEnvelope(cursor);

                    //dataSource.close();

                    Intent localIntent = new Intent();
                    Bundle b = new Bundle();
                    //b.putSerializable("gazzette", (Serializable) gazzetteList);
                    b.putSerializable(CURSOR_GAZZETTA, cursorEnvelope);
                    rec.send(Activity.RESULT_OK, b);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String downloadGazzette() throws Exception
    {
        String json = null;

        try
        {
            URL url = new URL(URL_WS);
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
}
