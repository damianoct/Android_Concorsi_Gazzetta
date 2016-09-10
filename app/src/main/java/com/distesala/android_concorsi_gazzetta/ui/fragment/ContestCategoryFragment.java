package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;

public class ContestCategoryFragment extends BaseFragment
{
    private CharSequence numberOfPublication;
    private CharSequence category;

    public static ContestCategoryFragment newInstance(CharSequence numberOfPublication, CharSequence category)
    {
        ContestCategoryFragment f = new ContestCategoryFragment();
        Bundle b = new Bundle();
        b.putCharSequence("numberOfPublication", numberOfPublication);
        b.putCharSequence("category", category);
        f.setArguments(b);
        return f;
    }

    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    public String getFragmentTitle()
    {
        return "Gazzetta n. " + numberOfPublication;
    }

    @Override
    public void searchFor(String s)
    {
        Log.i("fragment", category + " searchFor");
    }

    @Override
    public void onSearchFinished()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            CharSequence numberOfPublication = bundle.getCharSequence("numberOfPublication");
            this.numberOfPublication = numberOfPublication;

            CharSequence category = bundle.getCharSequence("category");
            this.category = category;
        }

        Log.i("fragment", category + "onCreate() querysearch -> " + querySearch);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fragmentListener.onSegueTransaction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_contest_category, container, false);
    }

}
