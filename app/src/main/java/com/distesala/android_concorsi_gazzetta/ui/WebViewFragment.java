package com.distesala.android_concorsi_gazzetta.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment
{
    private WebView webview;

    public WebViewFragment() { }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        webview = (WebView) rootView.findViewById(R.id.webView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Let's display the progress in the activity title bar, like the
        // browser app does.

        //webview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = getActivity();
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                Log.d("onprogesschange", "change! progress -> " + String.valueOf(progress));
                //activity.setProgress(progress * 1000);
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webview.setWebViewClient(new WebViewClient());


        webview.loadUrl("http://www.gazzettaufficiale.it/atto/vediMenuHTML?atto.dataPubblicazioneGazzetta=2016-08-26&atto.codiceRedazionale=16E04261&tipoSerie=concorsi&tipoVigenza=originario");
    }
}
