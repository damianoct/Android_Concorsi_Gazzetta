package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;
import com.distesala.android_concorsi_gazzetta.networking.Connectivity;
import com.distesala.android_concorsi_gazzetta.services.JSONDownloader;
import com.distesala.android_concorsi_gazzetta.services.JSONResultReceiver;
import com.distesala.android_concorsi_gazzetta.utils.Helper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class TextContestFragment extends HostSearchablesFragment implements JSONResultReceiver.Receiver,
                                                                            AppBarLayout.OnOffsetChangedListener
{
    protected static final String N_ARTICOLI = "nArticoli";
    protected static final String GAZZETTA_DATE_OF_PUB = "dateOfPublication";
    protected static final String GAZZETTA_NUM_OF_PUB = "numberOfPublication";
    protected static final String EMETTITORE = "emettitore";
    protected static final String CONTEST_ID = "contestID";

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 80;

    private FloatingActionButton mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private String dateOfPublication;
    private String contestID;
    private String numberOfPublication;
    private String emettitore;
    private JSONResultReceiver mReceiver;
    private String mURL;
    private List<String> articles;
    private int nArticoli;

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
        return "Gazzetta n. " + numberOfPublication;
    }

    @Override
    protected SearchableFragment getChild(int position)
    {
        return ContentFragment.newInstance((articles != null && !articles.isEmpty()) ? articles.get(position) : "", emettitore);
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
        else if (resultCode == Connectivity.CONNECTION_LOCKED)
        {
            Helper.showConnectionAlert(getActivity());
        }
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
        //tolgo la searchview momentaneamente, sarà abilitato nelle future iterazioni.
        setHasOptionsMenu(false);
        nArticoli = getArguments().getInt(N_ARTICOLI);
        //dateOfPublication = Helper.formatDate(getArguments().getString(GAZZETTA_DATE_OF_PUB), "dd MMMM yyyy");
        dateOfPublication = getArguments().getString(GAZZETTA_DATE_OF_PUB);
        numberOfPublication = getArguments().getString(GAZZETTA_NUM_OF_PUB);
        contestID = getArguments().getString(CONTEST_ID);
        emettitore = getArguments().getString(EMETTITORE);

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

        AppBarLayout appbar = (AppBarLayout) view.findViewById(R.id.appbarlayout);
        appbar.addOnOffsetChangedListener(this);

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

        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(handler);
    }

    private void shareURL()
    {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjectCondivisione));
        share.putExtra(Intent.EXTRA_TEXT, mURL);

        startActivity(Intent.createChooser(share, getString(R.string.condividiURL)));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i)
    {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).withEndAction(new Runnable() {
                    @Override
                    public void run()
                    {
                        mFab.setClickable(false);
                    }
                }).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).withEndAction(new Runnable() {
                    @Override
                    public void run()
                    {
                        mFab.setClickable(true);
                    }
                }).start();
            }
        }
    }
}