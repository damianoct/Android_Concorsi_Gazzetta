package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class GazzetteListExtendedFragment extends BaseFragment implements JSONResultReceiver.Receiver
{
    private JSONResultReceiver mReceiver;
    private ListView gazzetteList;
    private GazzettaCursorAdapter adapter;

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

    public GazzetteListExtendedFragment()
    {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("ExtendendFragment", "onCreateView()");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gazzettelist, container, false);
        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);
        gazzetteList.setNestedScrollingEnabled(true);

        gazzetteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //si potrebbe lanciare una nuova activity al posto di un nuovo fragment, ma conviene?

                //costruisco il bundle
                Bundle bundle = new Bundle();
                CharSequence numberOfPublication = ((TextView) arg1.findViewById(R.id.numberOfPublication)).getText();

                GazzettaFragment gazzettaFragment = GazzettaFragment.newInstance(numberOfPublication);


                //lancio un nuovo fragment (Up Navigation con Fragment)
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, gazzettaFragment)
                        .addToBackStack(HomeActivity.SEGUE_TRANSACTION)
                        .commit();

                //notifico alla home activity
                fragmentListener.onSegueTransaction();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        assert adapter != null;
        gazzetteList.setAdapter(adapter);
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
