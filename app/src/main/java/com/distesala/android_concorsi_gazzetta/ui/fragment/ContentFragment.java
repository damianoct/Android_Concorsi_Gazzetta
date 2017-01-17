package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;
import com.pnikosis.materialishprogress.ProgressWheel;

public class ContentFragment extends SearchableFragment
{
    protected static final String CONTENT = "content";
    protected static final String EMETTITORE = "emettitore";
    private String content;
    private String emettitore;
    private ProgressWheel progressWheel;

    public static ContentFragment newInstance(String content, String emettitore)
    {
        ContentFragment f = new ContentFragment();
        Bundle b = new Bundle(1);
        b.putString(CONTENT, content);
        b.putString(EMETTITORE, emettitore);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        content = getArguments().getString(CONTENT);
        emettitore = getArguments().getString(EMETTITORE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cardview, container, false);
        ProgressWheel progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        Animation animFadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
        progressWheel.setAnimation(animFadeOut);

        TextView contentTextView = (TextView) rootView.findViewById(R.id.content);
        TextView emettitoreTextView = (TextView) rootView.findViewById(R.id.emettitore);

        startProgressWheel(progressWheel);

        if(!content.isEmpty())
        {
            if(content.contains("FAILED"))
            {
                stopProgressWheel(progressWheel);
                Button reloadButton = (Button) rootView.findViewById(R.id.reloadButton);
                reloadButton.setVisibility(View.VISIBLE);
                reloadButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ((TextContestFragment) getParentFragment()).startContestDownload();
                    }
                });
            }
            else
            {
                stopProgressWheel(progressWheel);
                contentTextView.setText(content.replaceAll("\\s+", " "));
                emettitoreTextView.setText(emettitore);
            }
        }

        return rootView;
    }

    @Override
    protected void performSearch(String querySearch)
    {
        //da implementare nelle future iterazioni per cercare all'interno del content.
    }

    private void startProgressWheel(final ProgressWheel progressWheel)
    {
        Log.d("pulsante", "START Progress Wheel");
        progressWheel.post(new Runnable()
        {
            @Override
            public void run()
            {
                progressWheel.spin();
                progressWheel.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopProgressWheel(final ProgressWheel progressWheel)
    {
        Log.d("pulsante", "STOP Progress Wheel");

        progressWheel.post(new Runnable()
        {
            @Override
            public void run()
            {
                progressWheel.setVisibility(View.GONE);
                progressWheel.clearAnimation();
                progressWheel.stopSpinning();
            }
        });
    }

}
