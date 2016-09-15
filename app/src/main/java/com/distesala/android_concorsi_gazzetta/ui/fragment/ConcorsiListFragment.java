package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConcorsiListFragment extends HostSearchablesFragment
{
    private static String APPBAR_TITLE = "Concorsi";
    private static final String CONCORSI_FRAGMENT = String.valueOf(R.id.concorsi);
    private static final String IN_SCADENZA = "In Scadenza";
    private static final String PREFERITI = "Preferiti";

    private static final int IN_SCADENZA_POS = 0;
    private static final int PREFERITI_POS = 1;

    private static final String[] childTitles = new String[]{   IN_SCADENZA,
                                                                PREFERITI
                                                            };
    private String threshold;

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
        return R.layout.fragment_concorsi_list;
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        SearchableFragment f = null;
        Bundle bundle = getQueryBundleForPosition(position);

        switch (position)
        {
            case IN_SCADENZA_POS:
                f = ContestCategoryFragment.newInstance(bundle);
                break;
            case PREFERITI_POS:
                f = FavContestListFragment.newInstance(bundle);
                break;
        }

        return f;
    }

    @Override
    protected Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);
        String whereClause = null;
        String[] whereArgs = null;

        switch (position)
        {
            case IN_SCADENZA_POS:
                whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " <= date('now', '+" + threshold +
                                                                                " days') AND " +
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " >= date('now')";
                break;
            case PREFERITI_POS:
                whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE + "=?";
                whereArgs = new String[]{"1"};
                break;
        }

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //check for new preference value
        String key = getString(R.string.key_scadenza_threshold);
        threshold = String.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(key, 0));

        //controllare se devo refreshare il cursore a questa posizione
        //all'esatta posizione ci pensa la super classe.
        refreshQueryBundle();
    }
}
