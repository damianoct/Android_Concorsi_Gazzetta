package com.distesala.android_concorsi_gazzetta.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.ui.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //abilito il pulsante indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClearDBButtonClick(View v)
    {
        getContentResolver().delete(ConcorsiGazzettaContentProvider.GAZZETTE_URI, null, null);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(getString(R.string.key_clear_db), true).apply();
    }
}
