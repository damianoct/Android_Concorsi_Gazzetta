package com.distesala.android_concorsi_gazzetta.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.distesala.android_concorsi_gazzetta.IntroPageTransformer;
import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.adapter.IntroAdapter;

public class IntroActivity extends AppCompatActivity
{

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabDots);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new IntroPageTransformer());

        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed()
    {
    }
}
