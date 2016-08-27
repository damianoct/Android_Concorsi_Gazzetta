package com.distesala.android_concorsi_gazzetta.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment
{

    private ListView gazzetteList;
    private ArrayAdapter<CharSequence> adapter;

    public ListViewFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        gazzetteList = (ListView) rootView.findViewById(R.id.gazzetteList);

        //necessario per far scomparire l'appbar quando si scrolla
        gazzetteList.setNestedScrollingEnabled(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.days_names, R.layout.text_listview);
        gazzetteList.setAdapter(adapter);
    }


}
