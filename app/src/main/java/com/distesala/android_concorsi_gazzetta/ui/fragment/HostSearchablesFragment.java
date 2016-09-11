package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class HostSearchablesFragment extends BaseFragment
{
    //TODO se questo design è conveniente si deve aggiungere anche la gestione del viewpager e del tablayout

    private List<SearchableFragment> searchables;

    protected abstract SearchableFragment getChild(int position);

    public void notifyChildrenForSearch()
    {
        for(SearchableFragment sf: searchables)
            sf.performSearch(querySearch);
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
