package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.ui.HomeActivity;


public class ContestForGazzettaFragment extends HostSearchablesFragment
{
    private CharSequence numberOfPublication;
    private String filterArea = "";

    public static ContestForGazzettaFragment newInstance(Bundle bundle)
    {
        ContestForGazzettaFragment f = new ContestForGazzettaFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.fragment_contest_for_gazzetta;
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        Bundle itemBundle = getQueryBundleForPosition(position);

        return ContestCategoryFragment.newInstance(itemBundle);
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
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filtering, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() != R.id.menu_item_filtering && item.getItemId() != R.id.action_search)
        {
            switch (item.getItemId())
            {
                case R.id.action_no_filter:
                {
                    filterArea = "";
                    break;
                }

                case R.id.action_filter_amministrazioni_centrali:
                {
                    filterArea = getString(R.string.filter_amministrazioni_centrali);
                    break;
                }

                case R.id.action_filter_universita:
                {
                    filterArea = getString(R.string.filter_uni);
                    break;
                }

                case R.id.action_filter_aziende_sanitarie:
                {
                    filterArea = getString(R.string.filter_aziende_sanitarie);
                    break;
                }

                case R.id.action_filter_enti_pubblici_statali:
                {
                    filterArea = getString(R.string.filter_enti_pubblici_statali);
                    break;
                }

                case R.id.action_filter_enti_locali:
                {
                    filterArea = getString(R.string.filter_enti_locali);
                    break;
                }

                case R.id.action_filter_altri_enti:
                {
                    filterArea = getString(R.string.filter_altri_enti);
                    break;
                }
            }

            viewPager.getAdapter().notifyDataSetChanged();
            //refreshQueryBundle();
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected Bundle getQueryBundleForPosition(int position)
    {
        Bundle args = new Bundle(2);

        String category = getResources().getStringArray(R.array.contests_categories)[position];

        String whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =? AND "
                + GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA + " LIKE? AND "
                + GazzetteSQLiteHelper.ContestEntry.COLUMN_AREA + " LIKE? ";

        String[] whereArgs = new String[]{numberOfPublication.toString(), "%" + category + "%", "%" + filterArea + "%" };

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        Log.i("melinta", "------ INIT -----------------");
        Log.i("melinta", "getQueryBundlerForPosition -> " + position);
        Log.i("melinta", "whereClause -> " + whereClause);

        for(String s: whereArgs)
        {
            Log.i("melinta", "whereargs -> " + s);
        }


        return args;
    }

    protected Bundle buildCreationBundlerFor(int position)
    {
        Bundle args = new Bundle(2);

        String category = getResources().getStringArray(R.array.contests_categories)[position];

        String whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =? AND "
                + GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA + " LIKE? ";

        String[] whereArgs = new String[]{numberOfPublication.toString(), "%" + category + "%"};

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }
}
