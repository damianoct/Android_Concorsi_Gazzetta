package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.FavContestCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;


public class FavContestListFragment extends SearchableFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ListView contestsList;
    private CursorAdapter cursorAdapter;

    private void expandAppBarLayout()
    {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);
        CoordinatorLayout rootLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinator);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior != null)
            behavior.onNestedFling(rootLayout, appBarLayout, null, 0, -10000, true);
    }

    public static FavContestListFragment newInstance(Bundle queryBundle)
    {
        FavContestListFragment f = new FavContestListFragment();
        f.setArguments(queryBundle);
        return f;
    }

    @Override
    protected void performSearch(String querySearch)
    {
        Bundle args = getSearchBundle(querySearch);

        getLoaderManager().restartLoader(0, args, this);
    }

    @Nullable
    private Bundle getSearchBundle(String s)
    {
        Bundle args = null;

        if (s != null)
        {
            args = new Bundle(2);

            String whereClause = "(" +
                    GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE + " LIKE? OR " +
                    GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO + " LIKE? ) AND " +
                    GazzetteSQLiteHelper.ContestEntry.COLUMN_AREA + " LIKE? AND " +
                    GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE + " =? ";


            String[] whereArgs = new String[]{  "%" + s + "%",
                                                "%" + s + "%",
                                                queryBundle.getStringArray(BaseFragment.WHERE_ARGS)[1] ,
                                                "1" };

            args.putString(BaseFragment.WHERE_CLAUSE, whereClause);
            args.putStringArray(BaseFragment.WHERE_ARGS, whereArgs);
        }

        return args;
    }

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

        cursorAdapter = new FavContestCursorAdapter(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //same layout of contest category fragment
        View rootView = inflater.inflate(R.layout.fragment_contest_category, container, false);

        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessary to hide appbar when scrolling
        contestsList.setNestedScrollingEnabled(true);

        contestsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor c = ((CursorAdapter) parent.getAdapter()).getCursor();

                String dateOfPublication = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_DATE_OF_PUBLICATION));
                String contestID = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry._ID));
                String numberOfPublication = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION));
                String emettitore = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE));
                String tipologia = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA));
                String expiring = c.getString(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA));
                int nArticoli = c.getInt(c.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI));

                Bundle creationBundle = new Bundle(4);
                creationBundle.putBoolean(GazzetteListFragment.IS_FROM_SEGUE, true);
                creationBundle.putInt(TextContestFragment.N_ARTICOLI, nArticoli);
                creationBundle.putString(TextContestFragment.GAZZETTA_DATE_OF_PUB, dateOfPublication);
                creationBundle.putString(TextContestFragment.GAZZETTA_NUM_OF_PUB, numberOfPublication);
                creationBundle.putString(TextContestFragment.CONTEST_ID, contestID);
                creationBundle.putString(TextContestFragment.EMETTITORE, emettitore);
                creationBundle.putString(TextContestFragment.TIPOLOGIA, tipologia);
                creationBundle.putString(TextContestFragment.EXPIRING_DATE, expiring);

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
        if(data.getCount() == 0)
            expandAppBarLayout();
        cursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        cursorAdapter.swapCursor(null);
    }


}
