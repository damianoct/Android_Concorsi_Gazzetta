package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;


//Se ogni child fragment estente BaseFragment
//Problemissimi con la searchView che quando si switcha tab si mette la query a VUOTA. Dio santo
//vedi -> http://stackoverflow.com/questions/18148761/searchview-in-actionbar-problems-with-the-up-button

public class ContestCategoryFragment extends Fragment implements Searchable, LoaderManager.LoaderCallbacks<Cursor>
{
    private CharSequence numberOfPublication;
    private CharSequence category;

    public static ContestCategoryFragment newInstance(CharSequence numberOfPublication, CharSequence category)
    {
        ContestCategoryFragment f = new ContestCategoryFragment();
        Bundle b = new Bundle();
        b.putCharSequence("numberOfPublication", numberOfPublication);
        b.putCharSequence("category", category);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            CharSequence numberOfPublication = bundle.getCharSequence("numberOfPublication");
            this.numberOfPublication = numberOfPublication;

            CharSequence category = bundle.getCharSequence("category");
            this.category = category;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_contest_category, container, false);
    }

    @Override
    public void performSearch(String s)
    {
        Log.i("category", "[ " + category + " ] performSearch.");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
