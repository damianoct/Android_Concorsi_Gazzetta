package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;

public class ContentFragment extends SearchableFragment
{

    protected static final String CONTENT = "content";
    protected static final String EMETTITORE = "emettitore";
    private String content;
    private String emettitore;
    private TextView contentTextView;
    private TextView emettitoreTextView;

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
        contentTextView = (TextView) rootView.findViewById(R.id.content);
        contentTextView.setText(content.replaceAll("\\s+", " "));

        emettitoreTextView = (TextView) rootView.findViewById(R.id.emettitore);
        emettitoreTextView.setText(emettitore);

        return rootView;
    }

    @Override
    protected void performSearch(String querySearch)
    {

    }

}
