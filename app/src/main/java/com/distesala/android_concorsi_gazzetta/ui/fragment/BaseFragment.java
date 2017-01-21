package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
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

    private boolean isFromSegue = false;
    private boolean isDrawerTransaction = false;

    //ogni implementazione di BaseFragment possiede una searchViewItem nell'appbar.
    private MenuItem searchViewItem;

    public abstract String getFragmentName();

    public abstract String getFragmentTitle();

    public abstract void searchFor(String s);

    public abstract void onSearchFinished();

    protected String getSearchHint()
    {
        return "Cerca";
    }

    private void saveQuerySearch(String querySearch)
    {
        if(getArguments() != null)
            getArguments().putString(SEARCH_KEY, querySearch);
    }

    private void initSearchViewForMenu(Menu menu)
    {
        searchViewItem = menu.findItem(R.id.action_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchViewAndroidActionBar.setQueryHint(getSearchHint());

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
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (getActivity() instanceof FragmentListener)
        {
            fragmentListener = (FragmentListener) getActivity();
        }
        else
        {
            throw new RuntimeException(getActivity().toString()
                    + " must implement FragmentListener");
        }
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
        //restore state
        if (getArguments() != null)
            querySearch = getArguments().getString(SEARCH_KEY, null);
        else
            querySearch = null;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
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
    public void onDestroyView()
    {
        super.onDestroyView();
        if(isDrawerTransaction)
        {
            isDrawerTransaction = false;
            saveQuerySearch(null);
        }
        else
            saveQuerySearch(querySearch);

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item)
    {
        querySearch = "";
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item)
    {
        querySearch = null;
        saveQuerySearch(null);
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

    public final void onDrawerTransaction()
    {
        isDrawerTransaction = true;
        if(isSearchActive())
            MenuItemCompat.collapseActionView(searchViewItem);

    }
}