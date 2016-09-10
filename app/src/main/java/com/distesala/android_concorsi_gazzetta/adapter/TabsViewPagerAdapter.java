package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.ui.fragment.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by damiano on 09/09/16.
 */

public class TabsViewPagerAdapter<T extends Fragment> extends FragmentStatePagerAdapter
{

    private final List<T> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    public TabsViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
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


    //questo approccio presuppone che mi crei tutti i fragment
    //quindi posso creare inutilmente fragment che mai verranno utilizzati dall'utente.

    public void addFragment(T fragment, String title)
    {

        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public T getFragment(int position)
    {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return mFragmentTitleList.get(position);
    }
}
