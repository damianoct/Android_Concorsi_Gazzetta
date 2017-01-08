package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.GazzettaCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.networking.Connectivity;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;
import com.distesala.android_concorsi_gazzetta.utils.Helper;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Un grazie spassionato a https://github.com/pnikosis/materialish-progress per la progress wheel
 */

public class GazzetteListFragment extends BaseFragment implements JSONResultReceiver.Receiver,
                                                                    LoaderManager.LoaderCallbacks<Cursor>
{
    private JSONResultReceiver mReceiver;
    private ListView gazzetteList;
    private CursorAdapter adapter;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressWheel progressWheel;

    @Override
    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return "Concorsi Gazzetta";
    }

    @Override
    public void searchFor(String s)
    {
        Bundle args = getSearchBundle(s);

        getLoaderManager().restartLoader(0, args, this);
    }

    @Nullable
    private Bundle getSearchBundle(String s)
    {
        Bundle args = null;

        if(s != null)
        {
            args = new Bundle(2);
            String whereClause = ConcorsiGazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " LIKE? OR "
                    + ConcorsiGazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION + " LIKE? ";

            String[] whereArgs = new String[]{"%" + s + "%", "%" + s + "%"};

            args.putString(WHERE_CLAUSE, whereClause);
            args.putStringArray(WHERE_ARGS, whereArgs);

        }
        return args;
    }

    @Override
    public void onSearchFinished()
    {
        getLoaderManager().restartLoader(0, null, this);
    }

    public GazzetteListFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mReceiver = new JSONResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        //updateGazzette();

        adapter = new GazzettaCursorAdapter(getActivity(), null);

        setRetainInstance(true);
    }

    @Override
    protected String getSearchHint()
    {
        return getActivity().getResources().getString(R.string.gazzetteListHint);
    }

    private void updateGazzette()
    {
        Intent mServiceIntent = new Intent(getActivity(), JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.DOWNLOAD_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        getActivity().startService(mServiceIntent);

        progressWheel.spin();
        progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzettelist, container, false);

        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        progressWheel.setAnimation(animFadeOut);

        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);

        gazzetteList.setEmptyView(rootView.findViewById(R.id.emptyView));
        gazzetteList.setNestedScrollingEnabled(true);

        gazzetteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {

                CursorAdapter adapter = (CursorAdapter) arg0.getAdapter();
                CharSequence numberOfPublication = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(ConcorsiGazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION));

                Bundle creationBundle = new Bundle(2);
                creationBundle.putCharSequence("numberOfPublication", numberOfPublication);
                creationBundle.putBoolean(IS_FROM_SEGUE, true);
                ContestForGazzettaFragment contestForGazzettaFragment = ContestForGazzettaFragment.newInstance(creationBundle);


                //lancio un nuovo fragment (Up Navigation con Fragment)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, contestForGazzettaFragment)
                        .addToBackStack(HomeActivity.SEGUE_TRANSACTION)
                        .commit();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                updateGazzette();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        appBarLayout.setElevation(10);
        fragmentListener.onHomeTransaction();
        gazzetteList.setAdapter(adapter);
        updateGazzette();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            getLoaderManager().initLoader(0, null, this);
        }
        else if (resultCode == Activity.RESULT_CANCELED)
        {
            //Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG).show();
            getLoaderManager().initLoader(0, null, this);
        }
        else if (resultCode == Connectivity.CONNECTION_LOCKED)
        {
            Helper.showConnectionAlert(getActivity());
        }

        mSwipeRefreshLayout.setRefreshing(false);

        progressWheel.setVisibility(View.GONE);
        progressWheel.clearAnimation();
        progressWheel.stopSpinning();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String key = getActivity().getString(R.string.key_num_gazzette);
        int nRow = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0);
        String orderAndLimit = ConcorsiGazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION
                                    + " DESC" + " LIMIT " + nRow;

        return new CursorLoader(getActivity().getApplicationContext(),
                    ConcorsiGazzettaContentProvider.GAZZETTE_URI,
                    null, //projection (tutte le colonne)
                    (args != null ? args.getString(WHERE_CLAUSE): null), //selection
                    (args != null ? args.getStringArray(WHERE_ARGS) : null), //selectionArgs
                    orderAndLimit); //order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Bundle args = isSearchActive() ? getSearchBundle(querySearch) : null;

        //force restart for preference changed.
        getLoaderManager().restartLoader(0, args, this);
    }
}
