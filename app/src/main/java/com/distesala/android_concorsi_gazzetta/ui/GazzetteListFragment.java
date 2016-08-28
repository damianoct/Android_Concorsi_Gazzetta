package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.Nullable;


import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

public class GazzetteListFragment extends Fragment implements JSONResultReceiver.Receiver
{
    private static final String FRAGMENT_TAG = "10"; //must be a number for setFragment function

    private JSONResultReceiver mReceiver;
    private ListView gazzetteList;
    private GazzettaCursorAdapter adapter;
    private FragmentListener homeListener;

    public GazzetteListFragment() {}

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof FragmentListener)
        {
            homeListener = (FragmentListener) activity;
        }
        else
        {
            throw new RuntimeException(activity.toString()
                    + " must implement GazzetteFragmentListener");
        }
    }

    //modify app bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.app_name);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mReceiver = new JSONResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent mServiceIntent = new Intent(getActivity(), JSONDownloader.class);
        mServiceIntent.setAction(JSONDownloader.DOWNLOAD_GAZZETTA);
        mServiceIntent.putExtra("receiverTag", mReceiver);
        getActivity().startService(mServiceIntent);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if(adapter != null)
            gazzetteList.setAdapter(adapter);

        homeListener.onBackHome();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzettelist, container, false);
        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);
        gazzetteList.setNestedScrollingEnabled(true);

        gazzetteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //si potrebbe lanciare una nuova activity al posto di un nuovo fragment, ma conviene?


                //costruisco il bundle
                Bundle bundle = new Bundle();
                CharSequence numberOfPublication = ((TextView) arg1.findViewById(R.id.numberOfPublication)).getText();

                GazzettaFragment gazzettaFragment = GazzettaFragment.newInstance(numberOfPublication);


                //lancio un nuovo fragment (Up Navigation con Fragment)
                getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                    .replace(R.id.content_frame, gazzettaFragment, FRAGMENT_TAG)
                                        .addToBackStack(HomeActivity.SEGUE_TRANSACTION)
                                            .commit();

                //notifico alla home activity
                homeListener.onSegueTransaction();
            }
        });

        return rootView;
    }



    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            Cursor savedCursor = ((CursorEnvelope) resultData.getSerializable(JSONDownloader.CURSOR_GAZZETTA)).getCursor();
            adapter = new GazzettaCursorAdapter(getActivity(), savedCursor);
            gazzetteList.setAdapter(adapter);
        }

        if (resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG);
        }
    }
}
