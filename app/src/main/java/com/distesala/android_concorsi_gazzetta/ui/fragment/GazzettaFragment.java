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
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;

public class GazzettaFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private SimpleCursorAdapter adapterSimpleCursor;
    private CharSequence numberOfPublication;
    private ListView contestsList;


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

        getLoaderManager().initLoader(0, null, this);

        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzetta, container, false);
        contestsList = (ListView) rootView.findViewById(R.id.contestsList);

        //necessary to hide appbar when scrolling
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
