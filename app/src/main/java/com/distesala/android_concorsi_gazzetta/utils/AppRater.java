package com.distesala.android_concorsi_gazzetta.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * Thanks to Raghav Sood.
 */

public class AppRater
{
    private final static String APP_TITLE = "Concorsi Gazzetta";// App Name
    private final static String APP_PNAME = "com.distesala.android_concorsi_gazzetta";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor)
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Ti piace?");

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout buttonWrapper = new LinearLayout(mContext);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        wrapperParams.gravity = Gravity.CENTER_HORIZONTAL;
        wrapperParams.setMargins(0, 50, 0, 30);

        buttonWrapper.setLayoutParams(wrapperParams);
        buttonWrapper.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(30, 50, 0, 10);
        textParams.gravity = Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(textParams);


        tv.setText("Ti piace " + APP_TITLE + "? Faccelo sapere con una recensione. Il tuo contributo e' molto importante!");
        //tv.setPadding(20, 20, 20, 20);

        ll.addView(tv);

        /**
         * Button 1
         */

        Button b1 = new Button(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START | Gravity.BOTTOM;
        b1.setLayoutParams(params);
        b1.setText("Vota");
        b1.setBackgroundColor(Color.TRANSPARENT);
        b1.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        buttonWrapper.addView(b1);

        /**
         * Button 2
         */

        Button b2 = new Button(mContext);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER | Gravity.BOTTOM;
        b2.setLayoutParams(params2);
        b2.setText("Dopo");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        b2.setBackgroundColor(Color.TRANSPARENT);
        b2.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        buttonWrapper.addView(b2);

        /**
         * Button 3
         */

        Button b3 = new Button(mContext);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params3.gravity = Gravity.END | Gravity.BOTTOM;
        b3.setLayoutParams(params3);
        b3.setText("No, grazie");
        b3.setBackgroundColor(Color.TRANSPARENT);
        b3.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        buttonWrapper.addView(b3);

        ll.addView(buttonWrapper);

        dialog.setContentView(ll);
        dialog.show();
    }
}