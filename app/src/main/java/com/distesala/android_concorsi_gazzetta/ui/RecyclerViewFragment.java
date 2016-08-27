package com.distesala.android_concorsi_gazzetta.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment
{

    RecyclerView.LayoutManager layout_manager;
    RecyclerView gazzetteList;
    RecyclerViewAdapter adapter;

    public RecyclerViewFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        layout_manager = new LinearLayoutManager(getActivity());
        gazzetteList.setLayoutManager(layout_manager);

        adapter = new RecyclerViewAdapter(getResources().getStringArray(R.array.days_names));
        gazzetteList.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        gazzetteList = (RecyclerView) rootView.findViewById(R.id.days_list_2);
        return rootView;
    }

}
