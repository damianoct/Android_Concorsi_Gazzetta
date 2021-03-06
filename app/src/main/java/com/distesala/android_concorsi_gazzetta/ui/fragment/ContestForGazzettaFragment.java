package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;
import com.distesala.android_concorsi_gazzetta.utils.Helper;


public class ContestForGazzettaFragment extends HostSearchablesFragment
{
    private static final String FILTER_AREA = "filter";
    private CharSequence numberOfPublication;
    private int filterAreaId = R.id.action_no_filter;
    private Menu filterMenu;

    public static ContestForGazzettaFragment newInstance(Bundle bundle)
    {
        ContestForGazzettaFragment f = new ContestForGazzettaFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.fragment_contest_host;
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        Bundle itemBundle = getQueryBundleForPosition(position);

        return ContestFragment.newInstance(itemBundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            filterAreaId = savedInstanceState.getInt(FILTER_AREA);
        }

        Bundle bundle = this.getArguments();

        if (bundle != null)
        {
            CharSequence numberOfPublication = bundle.getCharSequence("numberOfPublication");
            this.numberOfPublication = numberOfPublication;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(FILTER_AREA, filterAreaId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filtering, menu);
        filterMenu = menu;
        Helper.selectItem(filterMenu, filterAreaId);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() != R.id.menu_item_filtering
                && item.getItemId() != R.id.action_search
                && !item.isChecked())
        {
            Helper.selectItem(filterMenu, item.getItemId());
            filterAreaId = item.getItemId();
            viewPager.getAdapter().notifyDataSetChanged();
            return true;
        }

        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getSearchHint()
    {
        return getActivity().getResources().getString(R.string.contestsListHint);
    }

    @Override
    protected String[] getTabTitles()
    {
        return getResources().getStringArray(R.array.contests_categories_titles);
    }

    @Override
    public String getFragmentName()
    {
        return HomeActivity.HOME_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return "Gazzetta n. " + numberOfPublication;
    }

    private Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);

        String category = getResources().getStringArray(R.array.contests_categories)[position];

        String whereClause = ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =? AND "
                + ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA + " LIKE? AND "
                + ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_AREA + " LIKE? ";

        String[] whereArgs = new String[]{numberOfPublication.toString(), "%" + category + "%", "%" + getString(Helper.getStringResourceForFilterAreaId(filterAreaId)) + "%" };

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }
}
