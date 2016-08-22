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

    /** quando cambia da portrait a landscape richiama comunque il metodo onCreate()
     *
     * riferimento -> https://developer.android.com/training/basics/activity-lifecycle/recreating.html
     * la soluzione più moderna è far fare tutto a un fragment
     * vedi -> http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
     * vedi -> http://stackoverflow.com/questions/16601286/android-save-and-restore-list-state-with-cursoradapter
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReceiver = new JSONResultReceiver(new Handler());
        Intent mServiceIntent = new Intent(this, JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.DOWNLOAD_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        startService(mServiceIntent);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mReceiver.setReceiver(this);

        /* Lancio l'intent diretto al servizio per scaricare
        * i JSON dal webservice */

        //JSONDownloader.startDownloadGazzetta(this);

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
            Log.i("Cursor", String.valueOf(cursor.getCount()));
            GazzettaCursorAdapter adapter = new GazzettaCursorAdapter(this, cursor);

            ListView listView = getListView();
            listView.setAdapter(adapter);
            Log.i("MainActivity", "Receive Result");
        }
    }
}
