package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;

public class SettingsFragment extends PreferenceFragment
                              implements SharedPreferences.OnSharedPreferenceChangeListener,
                                            LoaderManager.LoaderCallbacks<Cursor>
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume()
    {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        getLoaderManager().initLoader(0, null, this);

        updatePreferenceSummaries();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getPreferenceScreen().removePreference(findPreference(getString(R.string.first_launch)));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void updatePreferenceSummaries()
    {
        String key = getString(R.string.key_num_gazzette);
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary("Verranno visualizzate fin a un massimo di " +
                PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0) +
                " gazzette.");

        key = getString(R.string.key_scadenza_threshold);
        connectionPref = findPreference(key);
        connectionPref.setSummary("Saranno visualizzate solo i concorsi che scadono entro " +
                PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0) +
                " giorni.");

        key = getString(R.string.key_utilizzo_rete_dati);
        connectionPref = findPreference(key);
        connectionPref.setSummary(( PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(key, true)) ?
                                    getString(R.string.summary_rete_dati) :
                                    getString(R.string.summary_rete_wifi));

    }

    private void updateClearDBPref(int numberOfGazzette)
    {
        String key = getString(R.string.key_clear_db);
        Preference connectionPref = findPreference(key);

        connectionPref.setSummary("Il database contiene attualmente " +
                                    numberOfGazzette + " gazzette.");

        connectionPref.setEnabled(numberOfGazzette != 0);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        //Con sole tre preferences da aggiornare possiamo essere perdonati
        updatePreferenceSummaries();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(getActivity().getApplicationContext(),
                ConcorsiGazzettaContentProvider.GAZZETTE_URI,
                null, //projection (null is ALL COLUMNS)
                null, //selection
                null, //selectionArgs
                null); //order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        updateClearDBPref(data.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }
}
