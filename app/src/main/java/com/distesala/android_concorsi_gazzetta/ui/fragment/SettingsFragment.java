package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * A simple {@link Fragment} subclass.
 */

//TODO: Aggiungere il divider

public class SettingsFragment extends PreferenceFragment
                              implements SharedPreferences.OnSharedPreferenceChangeListener
{


    public SettingsFragment()
    {
        // Required empty public constructor
    }

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

        updatePreferenceSummaries();
    }

    private void updatePreferenceSummaries()
    {
        String key = getString(R.string.key_num_gazzette);
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary("Verranno salvate e visualizzate fin a un massimo di " +
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

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        //TODO: Provare con concat()

        //Con sole tre preferences possiamo essere perdonati
        updatePreferenceSummaries();

    }
}
