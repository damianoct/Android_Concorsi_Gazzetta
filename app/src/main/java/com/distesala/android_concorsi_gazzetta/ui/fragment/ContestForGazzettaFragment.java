package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;

import java.util.LinkedList;
import java.util.List;


public class ContestForGazzettaFragment extends BaseFragment
{
    private CharSequence numberOfPublication;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private List<Searchable> searchablesList;

    private void notifySearchables()
    {
        //TODO PROBLEMA: Quando switcho tab non ancora istanziate devo far cercare anche loro
        //bisogna gestire onPageSelected nel listener di viewpager adapter.
        for(Searchable s: searchablesList)
            s.performSearch(querySearch);
    }


    public static ContestForGazzettaFragment newInstance(CharSequence numberOfPublication)
    {
        ContestForGazzettaFragment f = new ContestForGazzettaFragment();
        Bundle b = new Bundle();
        b.putCharSequence("numberOfPublication", numberOfPublication);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        searchablesList = new LinkedList<>();

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            CharSequence numberOfPublication = bundle.getCharSequence("numberOfPublication");
            this.numberOfPublication = numberOfPublication;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onSegueTransaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_contest_for_gazzetta, container, false);
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

        //restore viewpager selected item.
        if (savedInstanceState != null)
        {
            final int position = savedInstanceState.getInt("currentViewPagerItem");
            viewPager.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager.setCurrentItem(position);
                }
            }, 100);
        }

    }


    @Override
    public void searchFor(String s)
    {
        //update all "active" (retained in memory) searchable tabs.
        notifySearchables();
    }

    @Override
    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return "Gazzetta n. " + numberOfPublication;
    }

    @Override
    public void onSearchFinished()
    {

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("currentViewPagerItem", viewPager.getCurrentItem());
    }

    private void setupViewPager(final ViewPager viewPager)
    {
        /*
            IMPORTANT: We must use getChildFragmentManager and not getFragmentManager!
            because we need to place fragment inside this Fragment.
            If we use getFragmentManager the framework manages the tab fragments like any other
            fragments in the application.
            Results? More than one searchView in the appbar :-)
         */

        viewPager.setAdapter(new TabAdapter(getChildFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                //onPageSelected notify new Searchable item for search.
                if(isSearchActive())
                    notifySearchables();
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    //si potrebbe fare pure un adapter generico (fatto nel package adapter), ma non posso sfruttare la creazione con gli array.
    //dovrei già istanziare tutto in memoria invece in questo modo istanzio solo quello che mi serve.

    class TabAdapter extends FragmentStatePagerAdapter
    {
        private Bundle buildQueryBundleForCategory(CharSequence category)
        {
            Bundle args = new Bundle(2);

            String whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =? AND "
                    + GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA + " LIKE? ";

            String[] whereArgs = new String[]{numberOfPublication.toString(), "%" + category + "%"};

            args.putString(WHERE_CLAUSE, whereClause);
            args.putStringArray(WHERE_ARGS, whereArgs);

            return args;
        }

        public TabAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            CharSequence category = getResources().getStringArray(R.array.contests_categories)[position];

            //build query bundle for child fragment and instantiate.
            Fragment f = ContestCategoryFragment.newInstance(buildQueryBundleForCategory(category));

            //We can use this adapter for non searchable fragments too, so check if it's a Searchable tab.
            if(f instanceof Searchable)
                searchablesList.add((Searchable) f);

            return f;
        }

        @Override
        public int getCount()
        {
            return getResources().getStringArray(R.array.contests_categories_titles).length;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return getResources().getStringArray(R.array.contests_categories_titles)[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            super.destroyItem(container, position, object);
            searchablesList.remove(object);
        }
    }

}
