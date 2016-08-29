package com.distesala.android_concorsi_gazzetta.ui;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

public class GazzettaFragment extends Fragment implements JSONResultReceiver.Receiver
{
    private JSONResultReceiver mReceiver;
    private ContestCursorAdapter adapter;
    private CharSequence numberOfPublication;

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
        getActivity().startService(mServiceIntent);
        setRetainInstance(true);

    }

    //modify app bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Gazzetta n. " + numberOfPublication);
    }

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
        super.onViewCreated(view, savedInstanceState);
        assert adapter != null;
        contestsList.setAdapter(adapter);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Cursor savedCursor = ((CursorEnvelope) resultData.getSerializable(JSONDownloader.CURSOR_CONTESTS)).getCursor();
            adapter = new ContestCursorAdapter(getActivity(), savedCursor);
            contestsList.setAdapter(adapter);
        }
    }
}
