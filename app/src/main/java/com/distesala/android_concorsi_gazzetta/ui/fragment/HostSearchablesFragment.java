package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class HostSearchablesFragment extends BaseFragment
{
    private List<SearchableFragment> searchables;

    protected abstract SearchableFragment getChild(int position);
    protected abstract String[] getTabTitles();

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
        //viewPager.setAdapter(new SearchableTabsAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.contests_categories_titles)));
        viewPager.setAdapter(   new SearchableTabsAdapter(getChildFragmentManager(),
                                getTabTitles()));

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
