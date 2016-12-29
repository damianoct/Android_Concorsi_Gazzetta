package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.utils.Helper;

public class ContestListFragment extends HostSearchablesFragment
{
    private static final String FILTER_AREA = "filter" ;
    private static String APPBAR_TITLE = "Concorsi";
    private static final String CONCORSI_FRAGMENT = String.valueOf(R.id.concorsi);
    private static final String IN_SCADENZA = "In Scadenza";
    private static final String PREFERITI = "Preferiti";
    private static final int IN_SCADENZA_POS = 0;
    private static final int PREFERITI_POS = 1;

    private static final String[] childTitles = new String[]{   IN_SCADENZA,
                                                                PREFERITI
                                                            };
    private Menu filterMenu;
    private int threshold;
    private int filterAreaId = R.id.action_no_filter;


    @Override
    protected String[] getTabTitles()
    {
        return childTitles;
    }

    @Override
    public String getFragmentName()
    {
        return CONCORSI_FRAGMENT;
    }

    @Override
    public String getFragmentTitle()
    {
        return APPBAR_TITLE;
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.fragment_contest_host;
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        SearchableFragment f = null;
        Bundle bundle = getQueryBundleForPosition(position);

        switch (position)
        {
            case IN_SCADENZA_POS:
                f = ContestFragment.newInstance(bundle);
                break;
            case PREFERITI_POS:
                f = FavContestFragment.newInstance(bundle);
                break;
        }

        return f;
    }

    private Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);
        String whereClause = null;
        String[] whereArgs = null;

        String key = getString(R.string.key_scadenza_threshold);
        threshold = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0);

        switch (position)
        {
            case IN_SCADENZA_POS:
                whereClause = ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " <= date('now', '+" + threshold +
                                                                                " days') AND " +
                        ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " >= date('now') AND " +
                        ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_AREA + " LIKE? ";
                        whereArgs = new String[]{"%" + getString(Helper.getStringResourceForFilterAreaId(filterAreaId)) + "%" };
                break;
            case PREFERITI_POS:
                whereClause = ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE + "=? AND " +
                                ConcorsiGazzetteSQLiteHelper.ContestEntry.COLUMN_AREA + " LIKE? ";
                whereArgs = new String[]{"1", "%" + getString(Helper.getStringResourceForFilterAreaId(filterAreaId)) + "%"};
                break;
        }

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            filterAreaId = savedInstanceState.getInt(FILTER_AREA);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(FILTER_AREA, filterAreaId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filtering, menu);
        filterMenu = menu;
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
    public void onResume()
    {
        super.onResume();

        String key = getString(R.string.key_scadenza_threshold);
        int newThreshold = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0);
        if(threshold != newThreshold)
            viewPager.getAdapter().notifyDataSetChanged();
    }
}
