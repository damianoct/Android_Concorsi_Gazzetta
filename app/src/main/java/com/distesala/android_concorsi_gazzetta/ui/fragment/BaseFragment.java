package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;

public abstract class BaseFragment extends Fragment implements MenuItemCompat.OnActionExpandListener
{
    protected static final String SEARCH_KEY = "search";

    protected FragmentListener fragmentListener;

    protected String querySearch = null;

    public abstract String getFragmentName();

    public abstract String getFragmentTitle();

    public abstract void searchFor(String s);

    public abstract void onSearchFinished();

    private void initSearchViewForMenu(Menu menu)
    {
        final MenuItem searchViewItem = menu.findItem(R.id.action_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        if(querySearch != null)
        {
            MenuItemCompat.expandActionView(searchViewItem);
            searchViewAndroidActionBar.setQuery(querySearch, true);
        }

        MenuItemCompat.setOnActionExpandListener(searchViewItem, this);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                querySearch = newText;
                searchFor(newText);
                return true;
            }
        });
    }

    public BaseFragment() { }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof FragmentListener)
        {
            fragmentListener = (FragmentListener) activity;
        }
        else
        {
            throw new RuntimeException(activity.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (querySearch != null)
        {
            outState.putString(SEARCH_KEY, querySearch);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(savedInstanceState != null) //restore state
        {
            querySearch = savedInstanceState.getString(SEARCH_KEY);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Log.i("lifecycle", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getFragmentTitle());
        inflater.inflate(R.menu.menu_options, menu);

        initSearchViewForMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onDisplayed(getFragmentName());
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item)
    {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item)
    {
        querySearch = null;
        onSearchFinished();
        return true;
    }

}