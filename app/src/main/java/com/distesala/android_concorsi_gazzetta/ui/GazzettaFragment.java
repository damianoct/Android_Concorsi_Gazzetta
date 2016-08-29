package com.distesala.android_concorsi_gazzetta.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;

public class GazzettaFragment extends Fragment
{
    private CharSequence numberOfPublication;

    public GazzettaFragment() { }

    private ListView gazzetteList;
    private ArrayAdapter<CharSequence> adapter;

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
