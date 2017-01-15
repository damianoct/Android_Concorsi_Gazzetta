package com.distesala.android_concorsi_gazzetta.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.distesala.android_concorsi_gazzetta.R;

public class IntroFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mBackgroundColor, mPage;

    public static IntroFragment newInstance(int backgroundColor, int page)
    {
        IntroFragment frag = new IntroFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, backgroundColor);
        b.putInt(PAGE, page);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(BACKGROUND_COLOR))
            throw new RuntimeException("Fragment must contain a \"" + BACKGROUND_COLOR + "\" argument!");
        mBackgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage)
        {
            case 0:
                layoutResId = R.layout.fragment_intro_start;
                break;
            case 1:
                layoutResId = R.layout.fragment_intro_gazzette;
                break;
            case 2:
                layoutResId = R.layout.fragment_intro_concorsi;
                break;
            default:
                layoutResId = R.layout.fragment_intro_end;
                break;
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);
        switch (mPage)
        {
            case 0:
                view.findViewById(R.id.skipButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        getActivity().finish();
                    }
                });
                break;
            case 1:
                //view.findViewById(R.id.concept).setBackgroundColor(Color.TRANSPARENT);
                String text;
                text = "<html><body><p align=\"justify\" style=\"line-height:1.5\">";
                text+= getString(R.string.intro_gazzette_text);
                text+= "</p></body></html>";
                //((WebView) view.findViewById(R.id.concept)).loadData(text, "text/html", "utf-8");
                break;
            case 2:
                //view.findViewById(R.id.concept).setBackgroundColor(Color.TRANSPARENT);
                text = "<html><body><p align=\"justify\" style=\"line-height:1.5\">";
                text+= getString(R.string.intro_concorsi_text);
                text+= "</p></body></html>";
                //((WebView) view.findViewById(R.id.concept)).loadData(text, "text/html", "utf-8");
                break;

            case 3:
                view.findViewById(R.id.endIntroButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        getActivity().finish();
                    }
                });
                break;
            default:
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.intro_background);
        background.setBackgroundColor(mBackgroundColor);
    }



}
