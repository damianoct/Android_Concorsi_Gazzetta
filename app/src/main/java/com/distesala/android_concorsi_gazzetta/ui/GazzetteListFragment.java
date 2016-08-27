package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SharedElementCallback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

public class GazzetteListFragment extends Fragment implements JSONResultReceiver.Receiver
{
    private static final String FRAGMENT_TAG = "10";

    private JSONResultReceiver mReceiver;
    private ListView gazzetteList;
    private GazzettaCursorAdapter adapter;
    private GazzetteListFragmentListener homeListener;

    public GazzetteListFragment() {}

    public interface GazzetteListFragmentListener
    {
        void onChoise();
        void onBackHome();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        //pulisco il back stack essendo il fragment HOME.

        if (activity instanceof GazzetteListFragmentListener)
        {
            homeListener = (GazzetteListFragmentListener) activity;
        }
        else
        {
            throw new RuntimeException(activity.toString()
                    + " must implement GazzetteFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
                                        .addToBackStack("SEGUE")
                                            .commit();

                //notifico alla home activity
                homeListener.onChoise();
            }
        });

        //getActivity().getSupportFragmentManager().popBackStack("segue", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*Log.i("BackStack", "---------------------------------------------------" );
        Log.i("Backstack", "Count -> " + String.valueOf(getActivity().getSupportFragmentManager().getBackStackEntryCount()));
        for(int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
        {
            Log.i("Backstack", getActivity().getSupportFragmentManager().getBackStackEntryAt(i).toString());
        }
        Log.i("BackStack", "---------------------------------------------------" );
        */
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
