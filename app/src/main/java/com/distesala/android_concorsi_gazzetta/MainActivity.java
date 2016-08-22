package com.distesala.android_concorsi_gazzetta;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;
import com.distesala.android_concorsi_gazzetta.ui.GazzettaCursorAdapter;

import java.util.List;

public class MainActivity extends ListActivity implements JSONResultReceiver.Receiver
{
    private JSONResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new JSONResultReceiver(new Handler());
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mReceiver.setReceiver(this);

        /* Lancio l'intent diretto al servizio per scaricare
        * i JSON dal webservice */

        //JSONDownloader.startDownloadGazzetta(this);
        Intent mServiceIntent = new Intent(this, JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.DOWNLOAD_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        startService(mServiceIntent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mReceiver.setReceiver(null);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == RESULT_OK)
        {
            Cursor cursor = ((CursorEnvelope) resultData.getSerializable(JSONDownloader.CURSOR_GAZZETTA)).getCursor();
            GazzettaCursorAdapter adapter = new GazzettaCursorAdapter(this, cursor);

            ListView listView = getListView();
            listView.setAdapter(adapter);
        }
    }
}
