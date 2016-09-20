package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;

import java.util.ArrayList;
import java.util.List;

public class TextContestFragment extends HostSearchablesFragment implements JSONResultReceiver.Receiver
{
    protected static final String N_ARTICOLI = "nArticoli";
    protected static final String GAZZETTA_DATE_OF_PUB = "dateOfPublication";
    protected static final String CONTEST_ID = "contestID";

    private int nArticoli;
    private String dateOfPublication;
    private String contestID;
    private JSONResultReceiver mReceiver;
    private String mURL;
    private List<String> articles;

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
        return ContentFragment.newInstance((articles != null && !articles.isEmpty()) ? articles.get(position) : "");
    }

    @Override
    protected String[] getTabTitles()
    {
        String[] titles = new String[nArticoli];

        for(int i = 0; i < nArticoli; i++)
            titles[i] = String.valueOf(i + 1);
        return titles;
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

        String[] whereArgs = new String[]{"70", "%" + category + "%"};

        args.putString(WHERE_CLAUSE, whereClause);
        args.putStringArray(WHERE_ARGS, whereArgs);

        return args;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            articles = resultData.getStringArrayList("articles");
            mURL = resultData.getString("url");

            //refresh adapter
            viewPager.getAdapter().notifyDataSetChanged();

        } else if (resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG).show();
        }
        /*else if (resultCode == Connectivity.CONNECTION_LOCKED)
        {
            showConnectionAlert();
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if(articles != null && !articles.isEmpty())
        {
            outState.putStringArrayList("articles", new ArrayList<>(articles));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        nArticoli = getArguments().getInt(N_ARTICOLI);
        dateOfPublication = getArguments().getString(GAZZETTA_DATE_OF_PUB);
        contestID = getArguments().getString(CONTEST_ID);

        //TODO mettere il controllo sulla connettivity!

        if(savedInstanceState == null)
        {
            mReceiver = new JSONResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent mServiceIntent = new Intent(getActivity(), JSONDownloader.class);
            mServiceIntent.putExtra(Gazzetta.DATE_OF_PUBLICATION, dateOfPublication);
            mServiceIntent.putExtra(Concorso.CONTEST_ID, contestID);
            mServiceIntent.setAction(JSONDownloader.DOWNLOAD_CONTEST);
            mServiceIntent.putExtra("receiverTag", mReceiver);

            getActivity().startService(mServiceIntent);
        }
        else
        {
            articles = savedInstanceState.getStringArrayList("articles");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //Sharing

        View.OnClickListener handler = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(v.getId() == R.id.fab)
                {
                    shareURL();
                }
            }
        };

        getActivity().findViewById(R.id.fab).setOnClickListener(handler);
    }

    private void shareURL()
    {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjectCondivisione));
        share.putExtra(Intent.EXTRA_TEXT, mURL);

        startActivity(Intent.createChooser(share, getString(R.string.condividiURL)));
    }

}