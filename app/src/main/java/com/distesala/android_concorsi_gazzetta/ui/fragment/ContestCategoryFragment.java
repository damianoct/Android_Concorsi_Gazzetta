package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;


public class ContestCategoryFragment extends SearchableFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ListView contestsList;
    private SimpleCursorAdapter adapterSimpleCursor;

    public static ContestCategoryFragment newInstance(Bundle queryBundle)
    {
        ContestCategoryFragment f = new ContestCategoryFragment();
        f.setArguments(queryBundle);

        return f;
    }

    public ContestCategoryFragment() { }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, queryBundle, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        queryBundle = getArguments();

        adapterSimpleCursor = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.contest_item,
                null, //cursor null
                new String[]    {
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE,
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO
                },
                new int[]       {
                        R.id.emettitore,
                        R.id.titolo
                }
                , 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_contest_category, container, false);

        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessary to hide appbar when scrolling
        contestsList.setNestedScrollingEnabled(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        contestsList.setAdapter(adapterSimpleCursor);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //force restart for preference changed.
        getLoaderManager().restartLoader(0, queryBundle, this);
    }

    @Override
    public void performSearch(String s)
    {
        //TODO implementare ricerca fragment child.
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(getActivity().getApplicationContext(), //context
                ConcorsiGazzettaContentProvider.CONTESTS_URI, //uri
                null, //ALL COLUMNS
                args.getString(BaseFragment.WHERE_CLAUSE), //selection
                args.getStringArray(BaseFragment.WHERE_ARGS), //selectionArgs
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.i("category", "contest category loadfinish, SIZE -> " + String.valueOf(data.getCount()));
        adapterSimpleCursor.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapterSimpleCursor.swapCursor(null);
    }
}
