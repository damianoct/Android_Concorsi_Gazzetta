package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class GazzetteListFragment extends BaseFragment implements JSONResultReceiver.Receiver,
                                                                    LoaderManager.LoaderCallbacks<Cursor>
{
    private JSONResultReceiver mReceiver;
    private ListView gazzetteList;
    private CursorAdapter adapter;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressWheel progressWheel;
    private SwipeRefreshLayout emptySwipeRefreshLayout;

    @Override
    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return "Gazzette";
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

    public static GazzetteListFragment newInstance(Bundle bundle)
    {
        GazzetteListFragment f = new GazzetteListFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mReceiver = new JSONResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        adapter = new GazzettaCursorAdapter(getActivity(), null);

        setRetainInstance(true);
    }

    @Override
    protected String getSearchHint()
    {
        return getActivity().getResources().getString(R.string.gazzetteListHint);
    }

    private void updateGazzette(boolean silentMode)
    {
        Intent mServiceIntent = new Intent(getActivity(), JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.DOWNLOAD_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        mServiceIntent.putExtra(JSONDownloader.SILENT_MODE, silentMode);
        getActivity().startService(mServiceIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzettelist, container, false);

        emptySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout_emptyView);

        ((TextView) rootView.findViewById(R.id.emptyTextView)).setText(R.string.home_empty_contest_list);

        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);

        gazzetteList.setNestedScrollingEnabled(true);
        gazzetteList.setEmptyView(emptySwipeRefreshLayout);

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
                updateGazzette(false);
            }
        });

        emptySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        emptySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                updateGazzette(false);
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(settings.getBoolean(getString(R.string.first_launch), true))
        {
            emptySwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    emptySwipeRefreshLayout.setRefreshing(true);
                }
            });
            settings.edit().putBoolean(getString(R.string.first_launch), false).apply();
            updateGazzette(false);
        }

        else if(getArguments().get("Date") != null) //notification
        {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });

            updateGazzette(false);
        }

        else if(savedInstanceState != null && savedInstanceState.getBoolean("isRefreshing"))
        {
            emptySwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    emptySwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        //se ho svuotato il database allora non aggiorno
        else if(settings.getBoolean(getString(R.string.key_clear_db), true)) {}
        else
            updateGazzette(true);
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
            getLoaderManager().initLoader(0, null, this);
        }
        else if (resultCode == Connectivity.CONNECTION_LOCKED)
        {
            if(!resultData.getBoolean(JSONDownloader.SILENT_MODE, false))
                Helper.showConnectionAlert(getActivity());
        }

        mSwipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        emptySwipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                emptySwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRefreshing", emptySwipeRefreshLayout.isRefreshing());
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
        Cursor c = adapter.swapCursor(data);
        if(c != null) c.close();
        if(data.getCount() > 0) PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(getString(R.string.key_clear_db), false).apply();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.changeCursor(null);
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
