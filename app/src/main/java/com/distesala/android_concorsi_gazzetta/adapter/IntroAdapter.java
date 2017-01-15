package com.distesala.android_concorsi_gazzetta.adapter;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.distesala.android_concorsi_gazzetta.ui.fragment.IntroFragment;

/**
 * Created by damiano on 13/01/17.
 */

public class IntroAdapter extends FragmentPagerAdapter
{

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        //return IntroFragment.newInstance(position == 0 || position == 3 ? Color.parseColor("#3089BD") : Color.parseColor("#F7E5D4"), position);
        switch (position)
        {
            case 0:
                return IntroFragment.newInstance(Color.parseColor("#3089BD"), position); // blue
            //case 2:
                //return IntroFragment.newInstance(Color.parseColor("#FFEEA764"), position); // orange
            default:
                return IntroFragment.newInstance(Color.parseColor("#F7E5D4"), position); // beige
        }
    }

    @Override
    public int getCount()
    {
        return 4;
    }

}
