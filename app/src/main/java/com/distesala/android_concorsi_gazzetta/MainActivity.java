package com.distesala.android_concorsi_gazzetta;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;

public class MainActivity extends ListActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //TODO: Inserire il codice per scaricre il JSON
        JSONDownloader.startDownloadGazzetta(this);

        //TODO: Inserire il codice per riempire la lista
    }
}
