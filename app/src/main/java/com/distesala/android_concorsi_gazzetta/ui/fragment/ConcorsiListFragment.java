package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConcorsiListFragment extends HostSearchablesFragment
{
    private static String APPBAR_TITLE = "Concorsi";
    private static final String CONCORSI_FRAGMENT = String.valueOf(R.id.concorsi);
    private static final String IN_SCADENZA = "In Scadenza";
    private static final String PREFERITI = "Preferiti";

    private static final int IN_SCADENZA_POS = 0;
    private static final int PREFERITI_POS = 1;

    private static final String[] childTitles = new String[]{   IN_SCADENZA,
                                                                PREFERITI
                                                            };
    private String threshold;

    private TabLayout tabLayout;

    @Override
    protected String[] getTabTitles()
    {
        return childTitles;
    }

    private AppBarLayout appBarLayout;

    public String getFragmentName()
    {
        return CONCORSI_FRAGMENT;
    }

    public String getFragmentTitle()
    {
        return APPBAR_TITLE;
    }


    @Override
    protected SearchableFragment getChild(int position)
    {
        Bundle bundle = getQueryBundleForPosition(position);

        return ContestCategoryFragment.newInstance(bundle);
    }

    @Override
    protected Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);
        String whereClause = null;
        String whereArgs = null;

        switch (position)
        {
            case IN_SCADENZA_POS:
                whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " <= date('now', '+" + threshold +
                                                                                " days') AND " +
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " >= date('now')";
                break;
            case PREFERITI_POS:
                //TODO: implementare dopo aver cambiato il database per aggiungere i preferiti
                //args.putStringArray(WHERE_ARGS, whereArgs);
                break;
        }

        args.putString(WHERE_CLAUSE, whereClause);


        return args;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_concorsi_list, container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //bug tablayout support design v22 -> workaround stackoverflow
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        appBarLayout.setElevation(0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //check for new preference value
        String key = getString(R.string.key_scadenza_threshold);
        threshold = String.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0));

        //controllare se devo refreshare il cursore a questa posizione
        //all'esatta posizione ci pensa la super classe.
        refreshQueryBundle();
    }
}
