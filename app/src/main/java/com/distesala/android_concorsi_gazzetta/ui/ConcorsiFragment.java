package com.distesala.android_concorsi_gazzetta.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;

import java.util.ArrayList;
import java.util.List;

public class ConcorsiFragment extends Fragment
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;

    private void setupViewPager(ViewPager viewPager)
    {
        //devo aggiungere Fragment dentro questo Fragment quindi devo prendere
        //il relativo FragmentManager con getChildFragmentManager

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new RecyclerViewFragment(), "In Scadenza");
        adapter.addFragment(new ListViewFragment(), "Preferiti");
        viewPager.setAdapter(adapter);
    }

    public ConcorsiFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_concorsi, container, false);
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

        //if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
        //getActivity().getSupportFragmentManager().executePendingTransactions();
            //getActivity().getSupportFragmentManager().popBackStackImmediate("segue", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*
        Log.i("BackStack", "---------------------------------------------------" );
        Log.i("Backstack", "Count -> " + String.valueOf(getActivity().getSupportFragmentManager().getBackStackEntryCount()));
        for(int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++)
        {
            Log.i("Backstack", getActivity().getSupportFragmentManager().getBackStackEntryAt(i).toString());
        }
        Log.i("BackStack", "---------------------------------------------------" );*/

    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }

}