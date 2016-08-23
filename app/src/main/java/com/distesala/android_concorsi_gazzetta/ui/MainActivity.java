package com.distesala.android_concorsi_gazzetta.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;

public class MainActivity extends FragmentActivity implements MainFragment.GazzetteFragmentListener
{
    private MainFragment mFragment;

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

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (MainFragment) fm.findFragmentByTag("main_fragment");

        if (mFragment == null) {
            mFragment = new MainFragment();
            fm.beginTransaction().add(mFragment, "main_fragment").commit();
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFragmentInteraction(Cursor cursor)
    {
        GazzettaCursorAdapter adapter = new GazzettaCursorAdapter(this, cursor);
        ListView listView =(ListView) findViewById(R.id.gazzetteList);
        listView.setAdapter(adapter);
    }
}
