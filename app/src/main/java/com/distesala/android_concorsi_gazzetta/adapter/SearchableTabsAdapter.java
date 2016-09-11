package com.distesala.android_concorsi_gazzetta.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.ui.fragment.HostSearchablesFragment;
import com.distesala.android_concorsi_gazzetta.ui.fragment.Searchable;



/**
 * Created by damiano on 09/09/16.
 */

public class SearchableTabsAdapter<T extends Fragment & Searchable> extends FragmentStatePagerAdapter
{

    private HostSearchablesFragment host;
    private String[] titles;

    public SearchableTabsAdapter(FragmentManager fm, HostSearchablesFragment host, String[] titles)
    {
        super(fm);
        this.host = host;
        this.titles = titles;
    }

    //non si può fare... problemi nella creazione del fragment

    @Override
    public Fragment getItem(int position)
    {
        //responsibility of the host for build the bundle.
        //Bundle itemBundle = host.getBundleForFragmentChild(position);
        //Fragment f = T.instantiate(host.getActivity(), "Child " + position, itemBundle); //non posso istanziare questi fragmenti. E' stato bello!

        //host.addSearchable((Searchable) f);
        Fragment f = Fragment.instantiate(host.getActivity(), "child");
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
        host.removeSearchable(object);
    }
}
