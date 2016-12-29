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

public abstract class BaseFragment extends Fragment implements MenuItemCompat.OnActionExpandListener,
                                                                SearchView.OnQueryTextListener
{
    protected static final String IS_FROM_SEGUE = "isFromSegue";
    protected static final String SEARCH_KEY = "search";
    protected static final String WHERE_CLAUSE = "whereClause";
    protected static final String WHERE_ARGS = "whereArgs";


    //comunicazione Fragment -> Activity
    protected FragmentListener fragmentListener;

    protected String querySearch = null;
    private String tmpSearch = null;

    private boolean isFromSegue = false;

    //ogni implementazione di BaseFragment possiede una searchViewItem nell'appbar.
    private MenuItem searchViewItem;

    public abstract String getFragmentName();

    public abstract String getFragmentTitle();

    public abstract void searchFor(String s);

    public abstract void onSearchFinished();

    private void initSearchViewForMenu(Menu menu)
    {
        searchViewItem = menu.findItem(R.id.action_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        //in landscape estende la searchbar per tutto lo schermo
        searchViewAndroidActionBar.setMaxWidth(Integer.MAX_VALUE);

        if(querySearch != null)
        {
            MenuItemCompat.expandActionView(searchViewItem);
            searchViewAndroidActionBar.setQuery(querySearch, true);
        }

        MenuItemCompat.setOnActionExpandListener(searchViewItem, this);

        searchViewAndroidActionBar.setOnQueryTextListener(this);

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
        Log.i("state", "state saved!");

        if (querySearch != null)
        {
            outState.putString(SEARCH_KEY, querySearch);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        isFromSegue = bundle != null && bundle.getBoolean(IS_FROM_SEGUE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        if(savedInstanceState != null) //restore state
            querySearch = savedInstanceState.getString(SEARCH_KEY);
        else
            querySearch = null;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(getFragmentTitle());
            inflater.inflate(R.menu.menu_search, menu);
        }

        initSearchViewForMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onFragmentDisplayed(getFragmentName());
        if (isFromSegue)
            fragmentListener.onSegueTransaction();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item)
    {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item)
    {
        Log.i("search", "collapsed");
        querySearch = null;
        onSearchFinished();
        return true;
    }

    protected boolean isSearchActive()
    {
        return querySearch != null;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
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
}