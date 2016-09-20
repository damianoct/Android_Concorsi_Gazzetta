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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.execptions.ResourceIDLayoutException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class HostSearchablesFragment extends BaseFragment
{
    private List<SearchableFragment> searchables;
    private SearchableTabsAdapter adapter;

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

    //questa è la funziona che fornisce il bundle per il refresh
    //può essere usata per fornire anche il bundle in fase di inizializzazione del child
    //se questo è uguale (come struttura) a quello per il refresh.
    @Nullable
    protected abstract Bundle getQueryBundleForPosition(int position);

    public final void refreshQueryBundle()
    {
        //int position = viewPager.getCurrentItem();

        for(SearchableFragment sf: searchables)
        {
            sf.onRefreshQueryBundle(getQueryBundleForPosition(sf.getPosition()));
        }
    }

    private void notifyChildrenForSearch()
    {
        for(SearchableFragment sf: searchables)
            sf.performSearch(querySearch);
    }

    @Override
    public final void searchFor(String s)
    {
        notifyChildrenForSearch();
    }

    @Override
    public final void onSearchFinished()
    {
        //Future implementations
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

        try
        {
            viewPager = getViewPager(rootView);
            tabLayout = getTabLayoutView(rootView);

        } catch (ResourceIDLayoutException e)
        {
            Log.e("FATAL", e.getMessage());
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

        //bug tablayout support design v22 -> workaround stackoverflow
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

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

        appBarLayout.setElevation(0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //check if viewpager is null for entry in backstack.
        if (viewPager != null)
            outState.putInt("currentViewPagerItem", viewPager.getCurrentItem());

        super.onSaveInstanceState(outState);
    }

    private void addSearchable(SearchableFragment sf)
    {
        searchables.add(sf);
    }

    private void removeSearchable(Object o)
    {
        searchables.remove(o);
    }

    protected final void setupViewPager()
    {
        adapter = new SearchableTabsAdapter(getChildFragmentManager(),
                getTabTitles());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position)
            {
                //expand appbar
                fragmentListener.expandAppBar();

                //onPageSelected notify new Searchable item for search.
                if(isSearchActive())
                    notifyChildrenForSearch();

            }

            @Override
            public void onPageScrollStateChanged(int state){}
        });
    }

    protected final void setupTabLayout()
    {
        if (getTabTitles().length > 10) //MAGIC NUMBER....
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
    }

    private class SearchableTabsAdapter extends FragmentStatePagerAdapter
    {
        private String[] titles;

        public SearchableTabsAdapter(FragmentManager fm, String[] titles)
        {
            super(fm);
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment f = getChild(position);
            SearchableFragment sf = (SearchableFragment) f;
            sf.setPosition(position);
            addSearchable(sf);

            return f;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        @Override
        public int getCount()
        {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            super.destroyItem(container, position, object);
            removeSearchable(object);
        }
    }

}
