package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

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


    protected abstract SearchableFragment getChild(int position);
    protected abstract String[] getTabTitles();
    //questa è la funziona che fornisce il bundle per il refresh
    //può essere usata per fornire anche il bundle in fase di inizializzazione del child
    //se questo è uguale (come struttura) a quello per il refresh.
    protected abstract Bundle getQueryBundleForPosition(int position);

    public final void refreshQueryBundle()
    {
        int position = viewPager.getCurrentItem();
        for(SearchableFragment sf: searchables)
            sf.onRefreshQueryBundle(getQueryBundleForPosition(position));
    }

    public void notifyChildrenForSearch()
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

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void addSearchable(SearchableFragment sf)
    {
        searchables.add(sf);
    }

    public void removeSearchable(Object o)
    {
        searchables.remove(o);
    }

    protected void setupViewPager(final ViewPager viewPager)
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
                //onPageSelected notify new Searchable item for search.
                if(isSearchActive())
                    notifyChildrenForSearch();

            }

            @Override
            public void onPageScrollStateChanged(int state){}
        });
    }

    class SearchableTabsAdapter extends FragmentStatePagerAdapter
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
            addSearchable((SearchableFragment) f);

            return f;
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
