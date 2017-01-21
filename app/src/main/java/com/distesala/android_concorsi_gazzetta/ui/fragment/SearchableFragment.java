package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class SearchableFragment extends Fragment
{
    protected Bundle queryBundle;
    protected Bundle searchBundle;

    protected abstract void performSearch(String querySearch);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        queryBundle = getArguments();


        if(getArguments().getBundle("searchBundle") != null)
            searchBundle = getArguments().getBundle("searchBundle");
        else if(savedInstanceState != null && savedInstanceState.getBundle("searchBundle") != null)
            searchBundle = savedInstanceState.getBundle("searchBundle");
        else
            searchBundle = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(searchBundle != null)
            outState.putBundle("searchBundle", searchBundle);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        getArguments().putBundle("searchBundle", searchBundle);
    }
}
