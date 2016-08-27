package com.distesala.android_concorsi_gazzetta.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.victor.loading.rotate.RotateLoading;

public class MainActivity extends AppCompatActivity implements MainFragment.GazzetteFragmentListener
{
    private MainFragment mFragment;
    private RotateLoading rLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rLoading = (RotateLoading) findViewById(R.id.rotateloading);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (MainFragment) fm.findFragmentByTag("main_fragment");

        if (mFragment == null)
        {
            mFragment = new MainFragment();
            fm.beginTransaction().add(mFragment, "main_fragment").commit();
            rLoading.start();
        }
    }

    @Override
    public void onFragmentInteraction(Cursor cursor)
    {
        dismissLoader();
        GazzettaCursorAdapter adapter = new GazzettaCursorAdapter(this, cursor);
        ListView listView = (ListView) findViewById(R.id.gazzetteList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onError()
    {
        dismissLoader();
        Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
    }

    private void dismissLoader()
    {
        if(rLoading.isStart())
        {
            rLoading.stop();
            rLoading.setVisibility(View.INVISIBLE);
        }
    }
}
