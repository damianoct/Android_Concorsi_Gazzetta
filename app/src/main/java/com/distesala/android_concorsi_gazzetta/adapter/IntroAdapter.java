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
        int bgColor = position != 0 ? Color.parseColor("#F7E5D4") : Color.parseColor("#3089BD");
        return IntroFragment.newInstance(bgColor, position);
    }

    @Override
    public int getCount()
    {
        return 5;
    }

}
