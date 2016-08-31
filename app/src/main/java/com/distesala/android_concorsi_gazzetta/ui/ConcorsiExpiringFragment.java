package com.distesala.android_concorsi_gazzetta.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConcorsiExpiringFragment extends Fragment
{

    private ListView concorsiExpiring;
    private ArrayAdapter<CharSequence> adapter;
    private FragmentListener homeListener;

    public ConcorsiExpiringFragment()
    {
        // Required empty public constructor
    }

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
                    + " must implement FragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_concorsi_expiring, container, false);
        concorsiExpiring = (ListView) rootView.findViewById(R.id.concorsiExpiring);

        //necessario per far scomparire l'appbar quando si scrolla
        concorsiExpiring.setNestedScrollingEnabled(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.days_names, R.layout.text_listview);
        concorsiExpiring.setAdapter(adapter);

        concorsiExpiring.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                homeListener.onSegueTransaction();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(getParentFragment());
                transaction.replace(R.id.content_frame, new WebViewFragment());
                transaction.addToBackStack(HomeActivity.SEGUE_TRANSACTION);
                transaction.commit();
            }
        });
    }


}