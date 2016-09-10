package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.ui.fragment.RecyclerViewFragment;


/**
 * Created by damiano on 09/09/16.
 */

public class CategoryTabsViewPagerAdapter extends FragmentStatePagerAdapter
{
    Context context;

    public CategoryTabsViewPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position)
    {

        return new RecyclerViewFragment();
    }

    @Override
    public int getCount()
    {
        return context.getResources().obtainTypedArray(R.array.contests_categories_titles).length();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return context.getResources().obtainTypedArray(R.array.contests_categories_titles).getString(position);
    }
}
