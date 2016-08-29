package com.distesala.android_concorsi_gazzetta.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public abstract class BaseFragment extends Fragment
{
    protected FragmentListener fragmentListener;

    public abstract String getFragmentName();

    public abstract String getFragmentTitle();

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
        fragmentListener.onBackHome();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getFragmentTitle());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onDisplayed(getFragmentName());
    }

}