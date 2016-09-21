package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.GazzettaCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.networking.Connectivity;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;
import com.distesala.android_concorsi_gazzetta.utils.Helper;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * A simple {@link Fragment} subclass.
 * Un razie spassionato a https://github.com/pnikosis/materialish-progress per la progress wheel
 */

public class GazzetteListFragment extends BaseFragment implements JSONResultReceiver.Receiver, LoaderManager.LoaderCallbacks<Cursor>
{
    //TODO provare a spostare le chiamate al loaderManager.init nei metodi onActivityCreated()
    //vedere -> http://stackoverflow.com/questions/14559573/getting-called-dostart-when-already-started-from-loadermanager-why

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
        //TODO QUERY GREZZA ricerca fra il numero di pubblicazione e la data di pubblicazione, da sistemare

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
            String whereClause = GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " LIKE? OR "
                    + GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION + " LIKE? ";

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
        updateGazzette();

        adapter = new GazzettaCursorAdapter(getActivity(), null);

        setRetainInstance(true);
    }

    private void updateGazzette()
    {
        progressWheel = (ProgressWheel) getActivity().findViewById(R.id.progress_wheel);
        Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        progressWheel.setAnimation(animFadeOut);

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
        Log.d("ExtendendFragment", "onCreateView()");

        super.onCreateView(inflater, container, savedInstanceState);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzettelist, container, false);

        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);
        gazzetteList.setNestedScrollingEnabled(true);

        gazzetteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {

                //CharSequence numberOfPublication = ((TextView) arg1.findViewById(R.id.numberOfPublication)).getText();

                CursorAdapter adapter = (CursorAdapter) arg0.getAdapter();
                CharSequence numberOfPublication = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION));

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
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        //Non ho messo switch case perchè mi secco a cambiare

        if (resultCode == Activity.RESULT_OK)
        {
            getLoaderManager().initLoader(0, null, this);
        }
        else if (resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG).show();
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
        String orderAndLimit = GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA
                                    + " DESC" + " LIMIT " + nRow;

        return new CursorLoader(getActivity().getApplicationContext(),
                    ConcorsiGazzettaContentProvider.GAZZETTE_URI,
                    null, //projection (null is ALL COLUMNS)
                    (args != null ? args.getString(WHERE_CLAUSE): null), //selection
                    (args != null ? args.getStringArray(WHERE_ARGS) : null), //selectionArgs
                    orderAndLimit); //order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.i("pio", "gazzette list finished, cursor -> " + String.valueOf(data.getCount()));
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        Log.i("pio", "gazzette list loader RESET");
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
