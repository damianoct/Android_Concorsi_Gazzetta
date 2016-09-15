package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

public class TextContestFragment extends HostSearchablesFragment
{
    public static TextContestFragment newInstance(Bundle bundle)
    {
        TextContestFragment f = new TextContestFragment();
        f.setArguments(bundle);
        return f;
    }

    public String getFragmentName()
    {
        return String.valueOf(R.id.concorsi);
    }

    public String getFragmentTitle()
    {
        return "SUCA";
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        return ContestCategoryFragment.newInstance(getQueryBundleForPosition(position));
    }

    @Override
    protected String[] getTabTitles()
    {
        return new String[] {"1", "2"};
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.fragment_text_contest;
    }

    @Override
    protected Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);

        String category = getResources().getStringArray(R.array.contests_categories)[position];

        String whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =? AND "
                + GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA + " LIKE? ";

        String[] whereArgs = new String[]{"73", "%" + category + "%"};

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }
}
