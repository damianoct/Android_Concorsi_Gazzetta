package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.ContestCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;


public class ContestCategoryFragment extends SearchableFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ListView contestsList;
    private CursorAdapter cursorAdapter;

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

        cursorAdapter = new ContestCursorAdapter(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_contest_category, container, false);

        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessary to hide appbar when scrolling
        contestsList.setNestedScrollingEnabled(true);

        contestsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor c = cursorAdapter.getCursor();

                String dateOfPublication = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_DATE_OF_PUBLICATION));
                String contestID = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry._ID));
                int nArticoli = c.getInt(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI));

                Bundle creationBundle = new Bundle(4);
                creationBundle.putBoolean(GazzetteListFragment.IS_FROM_SEGUE, true);
                creationBundle.putInt(TextContestFragment.N_ARTICOLI, nArticoli);
                creationBundle.putString(TextContestFragment.GAZZETTA_DATE_OF_PUB, dateOfPublication);
                creationBundle.putString(TextContestFragment.CONTEST_ID, contestID);

                TextContestFragment textContestFragment = TextContestFragment.newInstance(creationBundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, textContestFragment)
                        .addToBackStack(HomeActivity.SEGUE_TRANSACTION)
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        contestsList.setAdapter(cursorAdapter);
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
        Log.i("TANO", "contest category loadfinish, SIZE -> " + String.valueOf(data.getCount()));
        cursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        cursorAdapter.swapCursor(null);
    }
}
