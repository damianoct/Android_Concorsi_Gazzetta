package com.distesala.android_concorsi_gazzetta.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.distesala.android_concorsi_gazzetta.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //abilito il pulsante indietro
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        WebView wb = (WebView) findViewById(R.id.webView);
        wb.getSettings().setJavaScriptEnabled(true);


        wb.loadUrl("https://privacypolicies.com/privacy/view/5ee0b653f590728db6857e0aa13db8ce");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
