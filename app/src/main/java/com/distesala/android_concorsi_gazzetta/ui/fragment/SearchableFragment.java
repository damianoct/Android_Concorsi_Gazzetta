package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class SearchableFragment extends Fragment
{
    //ogni SearchableFragment possiede un Bundle che utilizza per indirizzare il loader inizialmente.
    //si può sostituire con getArguments()
    protected Bundle queryBundle;
    //il bundle per indirizzare la ricerca
    protected Bundle searchBundle;

    protected abstract void performSearch(String querySearch);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queryBundle = getArguments();
        if(savedInstanceState != null)
        {
            Log.i("lifecycle", "restore");
            searchBundle = savedInstanceState.getBundle("searchBundle");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.i("lifecycle", "saveInstance");
        if(searchBundle != null)
            outState.putBundle("searchBundle", searchBundle);
    }
}
