package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.distesala.android_concorsi_gazzetta.database.CursorEnvelope;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

public class MainFragment extends Fragment implements JSONResultReceiver.Receiver
{
    private JSONResultReceiver mReceiver;
    private GazzetteFragmentListener mListener;
    private Cursor savedCursor;

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
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (activity instanceof GazzetteFragmentListener)
        {
            mListener = (GazzetteFragmentListener) activity;
        }
        else
        {
            throw new RuntimeException(activity.toString()
                    + " must implement GazzetteFragmentListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /* Metodo di Receiver*/

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            savedCursor = ((CursorEnvelope) resultData.getSerializable(JSONDownloader.CURSOR_GAZZETTA)).getCursor();

            mListener.onFragmentInteraction(savedCursor);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (savedCursor != null)
        {
            mListener.onFragmentInteraction(savedCursor);
        }
    }

    public interface GazzetteFragmentListener
    {
        void onFragmentInteraction(Cursor cursor);
    }
}
