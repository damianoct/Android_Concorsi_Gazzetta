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
    private SearchableViewPagerAdapter adapter;

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

    private void notifyChildrenForSearch()
    {
        for(SearchableFragment sf: searchables)
            sf.performSearch(querySearch);

        Log.i("notify", "numberOfSearchables -> " + searchables.size());
    }

    @Override
    public final void searchFor(String s) //final!
    {
        notifyChildrenForSearch();
    }

    @Override
    public final void onSearchFinished() //final!
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

    private void setupViewPager()
    {
        //sono già dentro un fragment, quindi devo usare getChildFragmentManager.
        adapter = new SearchableViewPagerAdapter(getChildFragmentManager(),
                getTabTitles());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position)
            {
                //expand appbar
                fragmentListener.expandAppBar();

                //onPageSelected notifica gli altri searchables.
                if(isSearchActive())
                    notifyChildrenForSearch();

            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void setupTabLayout()
    {
        if (getTabTitles().length > 10) //MAGIC NUMBER....
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
    }

    private class SearchableViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private String[] titles;

        public SearchableViewPagerAdapter(FragmentManager fm, String[] titles)
        {
            super(fm);
            this.titles = titles;
        }

        //questa callback NON viene richiamata in seguito alla rotazione.
        @Override
        public Fragment getItem(int position)
        {
            Log.i("viewpager", "getItem");
            Fragment f = getChild(position);
            return f;
        }

        //questa callback viene richiamata SEMPRE anche in seguito alla rotazione.
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            SearchableFragment sf = (SearchableFragment) super.instantiateItem(container, position);
            addSearchable(sf);
            return sf;
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
