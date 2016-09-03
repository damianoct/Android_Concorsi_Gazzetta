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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.distesala.android_concorsi_gazzetta.R;

public abstract class BaseFragment extends Fragment
{
    protected FragmentListener fragmentListener;

    public abstract String getFragmentName();

    public abstract String getFragmentTitle();

    public abstract void searchFor(String s);

    public abstract void onSearchFinished();

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
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getFragmentTitle());
        inflater.inflate(R.menu.menu_options, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchViewItem, new MenuItemCompat.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                Log.d("search", "search expanded");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                Log.d("search", "search collapsed");
                onSearchFinished();
                return true;
            }
        });

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);

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
                searchFor(newText);
                return true;
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onDisplayed(getFragmentName());
    }

}