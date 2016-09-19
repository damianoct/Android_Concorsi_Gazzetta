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
    private String content;
    private TextView contentTextView;

    public static ContentFragment newInstance(String content)
    {
        ContentFragment f = new ContentFragment();
        Bundle b = new Bundle(1);
        b.putString(CONTENT, content);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        content = getArguments().getString(CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //TODO alcuni concorsi non si visualizzano tutti, controllare il replace ALL...
        //esempio comune di canonica d'adda -> ENTI LOCALI, gazzetta 73.

        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        contentTextView = (TextView) rootView.findViewById(R.id.content);
        contentTextView.setMovementMethod(new ScrollingMovementMethod());
        contentTextView.setText(content.replaceAll("\\s+", " "));
        return rootView;
    }

    @Override
    protected void performSearch(String querySearch)
    {

    }

}
