package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.ContestCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContestFragment extends SearchableFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private ListView contestsList;
    private CursorAdapter cursorAdapter;
    private View emptyView;
    private ProgressWheel progressWheel;


    public static ContestFragment newInstance(Bundle queryBundle)
    {
        ContestFragment f = new ContestFragment();
        f.setArguments(queryBundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cursorAdapter = new ContestCursorAdapter(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_contest, container, false);

        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        progressWheel.setAnimation(animFadeOut);

        emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);

        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessary to hide appbar when scrolling
        contestsList.setNestedScrollingEnabled(true);

        contestsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor c = ((CursorAdapter) parent.getAdapter()).getCursor();

                String dateOfPublication = c.getString(c.getColumnIndex(ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_DATE_OF_PUBLICATION));
                String contestID = c.getString(c.getColumnIndex(ConcorsiGazzetteSQLiteHelper.ContestEntry._ID));
                String numberOfPublication = c.getString(c.getColumnIndex(ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION));
                String emettitore = c.getString(c.getColumnIndex(ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE));
                int nArticoli = c.getInt(c.getColumnIndex(ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI));

                Bundle creationBundle = new Bundle(4);
                creationBundle.putBoolean(GazzetteListFragment.IS_FROM_SEGUE, true);
                creationBundle.putInt(TextContestFragment.N_ARTICOLI, nArticoli);
                creationBundle.putString(TextContestFragment.GAZZETTA_DATE_OF_PUB, dateOfPublication);
                creationBundle.putString(TextContestFragment.GAZZETTA_NUM_OF_PUB, numberOfPublication);
                creationBundle.putString(TextContestFragment.CONTEST_ID, contestID);
                creationBundle.putString(TextContestFragment.EMETTITORE, emettitore);

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
        progressWheel.spin();
        progressWheel.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(0, searchBundle != null ? concatenateBundles() : queryBundle, this);
    }

    @Override
    public void performSearch(String s)
    {
        searchBundle = getSearchBundle(s);

        getLoaderManager().restartLoader(0, concatenateBundles(), this);
    }

    @Nullable
    private Bundle getSearchBundle(String s)
    {
        Bundle args = null;

        if(s != null)
        {
            args = new Bundle(2);
            List<String> listArgs = new ArrayList<>();

            String whereClause = "(" +
                    ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE + " LIKE? OR " +
                    ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO + " LIKE? ) AND ";

            listArgs.add("%" + s + "%");
            listArgs.add("%" + s + "%");

            args.putString(BaseFragment.WHERE_CLAUSE, whereClause);
            args.putStringArray(BaseFragment.WHERE_ARGS, listArgs.toArray(new String[0]));
        }

        return args;
    }

    private Bundle concatenateBundles()
    {
        Bundle args = new Bundle(2);

        String whereClause;
        List<String> listArgs = new ArrayList<>();

        whereClause = searchBundle.getString(BaseFragment.WHERE_CLAUSE) + queryBundle.getString(BaseFragment.WHERE_CLAUSE);
        listArgs.addAll(Arrays.asList(searchBundle.getStringArray(BaseFragment.WHERE_ARGS)));
        listArgs.addAll(Arrays.asList(queryBundle.getStringArray(BaseFragment.WHERE_ARGS)));

        args.putString(BaseFragment.WHERE_CLAUSE, whereClause);
        args.putStringArray(BaseFragment.WHERE_ARGS, listArgs.toArray(new String[0]));

        return args;
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
        progressWheel.setVisibility(View.GONE);
        progressWheel.clearAnimation();
        progressWheel.stopSpinning();
        cursorAdapter.changeCursor(data);
        emptyView.setVisibility(data.getCount() > 0 ? View.INVISIBLE : View.VISIBLE);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        cursorAdapter.changeCursor(null);
    }
}
