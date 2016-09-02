package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.ContestCursorAdapter;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

public class GazzettaFragment extends BaseFragment implements JSONResultReceiver.Receiver, LoaderManager.LoaderCallbacks<Cursor>
{
    private JSONResultReceiver mReceiver;
    private ContestCursorAdapter adapter;
    private SimpleCursorAdapter adapterSimpleCursor;
    private CharSequence numberOfPublication;

    @Override
    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return "Gazzetta n. " + numberOfPublication;
    }

    @Override
    public void searchFor(String s)
    {

    }

    @Override
    public void onSearchFinished()
    {

    }

    public GazzettaFragment() { }

    private ListView contestsList;

    public static GazzettaFragment newInstance(CharSequence numberOfPublication)
    {
        GazzettaFragment gf = new GazzettaFragment();
        Bundle b = new Bundle();
        b.putCharSequence("numberOfPublication", numberOfPublication);
        gf.setArguments(b);
        return gf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //the fragment would update the app bar
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            CharSequence numberOfPublication = bundle.getCharSequence("numberOfPublication");
            this.numberOfPublication = numberOfPublication;
        }

        mReceiver = new JSONResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent mServiceIntent = new Intent(getActivity(), JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.GET_CONTEST_FOR_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        mServiceIntent.putExtra("gazzettaNumberOfPublication", numberOfPublication);

        //getActivity().startService(mServiceIntent);

        //Utilizzo il CursorLoader
        getLoaderManager().initLoader(0, null, this);

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

        setRetainInstance(true);

    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Gazzetta n. " + numberOfPublication);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzetta, container, false);
        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessario per far scomparire l'appbar quando si scrolla
        contestsList.setNestedScrollingEnabled(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        //super.onViewCreated(view, savedInstanceState);
        contestsList.setAdapter(adapterSimpleCursor);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Cursor savedCursor = ((CursorEnvelope) resultData.getSerializable(JSONDownloader.CURSOR_CONTESTS)).getCursor();
            adapter = new ContestCursorAdapter(getActivity(), savedCursor);
            contestsList.setAdapter(adapter);
            getLoaderManager().restartLoader(0, null, this);

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onSegueTransaction();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(getActivity().getApplicationContext(), ConcorsiGazzettaContentProvider.CONTESTS_URI,
                null, null, new String[] { numberOfPublication.toString() } , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.i("loader", "gazzettafragment loadfinish");
        adapterSimpleCursor.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
