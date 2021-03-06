package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.execptions.ResourceIDLayoutException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by damiano on 01/07/18.
 */

public abstract class ContestHostSearchablesFragment extends BaseFragment
{
    private List<SearchableFragment> searchables;

    protected ViewPager viewPager;
    protected TabLayout tabLayout;
    protected AppBarLayout appBarLayout;

    private TabLayout getTabLayoutView(View rootView) throws ResourceIDLayoutException
    {
        TabLayout t = (TabLayout) rootView.findViewById(R.id.tabs);
        if(t == null)
            throw new ResourceIDLayoutException(getResources().getString(R.string.layout_tablayout_exeception));
        else
            return t;
    }

    private ViewPager getViewPager(View rootView) throws ResourceIDLayoutException
    {
        ViewPager v = (ViewPager) rootView.findViewById(R.id.viewpager);
        if(v == null)
            throw new ResourceIDLayoutException(getResources().getString(R.string.layout_viewpager_exception));
        else
            return v;
    }

    protected abstract SearchableFragment getChild(int position);
    protected abstract String[] getTabTitles();
    protected abstract int getLayoutResource();

    private void notifyChildrenForSearch()
    {
        getChildFragmentManager().executePendingTransactions();

        for(SearchableFragment sf: searchables)
            if (sf.isAdded())
                sf.performSearch(querySearch);
    }

    @Override
    public final void searchFor(String s) //final!
    {
        notifyChildrenForSearch();
    }

    @Override
    public final void onSearchFinished() //final!
    {
        //Future implementations
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(isSearchActive())
            notifyChildrenForSearch();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        searchables = new LinkedList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(getLayoutResource(), container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarlayout);

        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(rootView, "elevation", 0));
        appBarLayout.setStateListAnimator(stateListAnimator);

        try
        {
            viewPager = getViewPager(rootView);
            tabLayout = getTabLayoutView(rootView);

        } catch (ResourceIDLayoutException e)
        {
            getActivity().finish();
        }

        setupViewPager();
        setupTabLayout();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final int position;
        if(getArguments() != null)
            position = getArguments().getInt("currentViewPagerItem",0);
        else if (savedInstanceState != null)
            position = savedInstanceState.getInt("currentViewPagerItem",0);
        else
            position = 0;

        tabLayout.setupWithViewPager(viewPager);

        /*tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        }, 100);
        viewPager.postDelayed(new Runnable() {

            @Override
            public void run() {
                viewPager.setCurrentItem(position, true);
            }
        },150);*/

        /*appBarLayout.postDelayed(new Runnable() {

            @Override
            public void run()
            {
                appBarLayout.setElevation(0);
            }
        },150);*/


    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (viewPager != null)
            outState.putInt("currentViewPagerItem", viewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (viewPager != null)
            getArguments().putInt("currentViewPagerItem", viewPager.getCurrentItem());
    }

    private void addSearchable(SearchableFragment sf)
    {
        searchables.add(sf);
    }

    private void removeSearchable(Object o)
    {
        searchables.remove(o);
    }

    private void setupViewPager()
    {
        //sono già dentro un fragment, quindi devo usare getChildFragmentManager.
        SearchableViewPagerAdapter adapter = new SearchableViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position)
            {
                //expand appbar
                fragmentListener.expandAppBar();

                //onPageSelected notifica gli altri searchables.
                if(isSearchActive())
                    notifyChildrenForSearch();

            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    protected void deleteLoadingTab()
    {
        SearchableViewPagerAdapter adapter = (SearchableViewPagerAdapter) viewPager.getAdapter();
        adapter.deleteTemporaryTab();
    }


    protected void addTab(String title)
    {
        SearchableViewPagerAdapter adapter = (SearchableViewPagerAdapter) viewPager.getAdapter();
        adapter.addTab((title));
    }

    private void setupTabLayout()
    {
        if (getTabTitles().length > 10) //MAGIC NUMBER....
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
    }

    private class SearchableViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private final ArrayList<CharSequence> titles = new ArrayList<>();



        public SearchableViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        //questa callback NON viene richiamata in seguito alla rotazione.
        @Override
        public Fragment getItem(int position)
        {
            Fragment f = getChild(position);
            return f;
        }

        @Override
        public void finishUpdate(ViewGroup container)
        {
            //poor solution -> http://stackoverflow.com/questions/41650721/attempt-to-invoke-virtual-method-android-os-handler-android-support-v4-app-frag
            try
            {
                super.finishUpdate(container);
            }
            catch (NullPointerException nullPointerException)
            {
                Log.d("nullpointer","Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }

        //questa callback viene richiamata SEMPRE anche in seguito alla rotazione.
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            SearchableFragment sf = (SearchableFragment) super.instantiateItem(container, position);
            addSearchable(sf);
            return sf;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        @Override
        public int getCount()
        {
            return titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            super.destroyItem(container, position, object);
            removeSearchable(object);
        }

        private void deleteTemporaryTab()
        {
            titles.remove(0);
            notifyDataSetChanged();
        }

        private void addTab(String title)
        {
            titles.add(title);
            if(titles.size() > 10)
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            //notifyDataSetChanged();

        }
    }

}
