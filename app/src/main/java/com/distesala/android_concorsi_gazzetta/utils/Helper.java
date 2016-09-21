package com.distesala.android_concorsi_gazzetta.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.distesala.android_concorsi_gazzetta.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by damiano on 20/09/16.
 */
public class Helper
{
    public static int getStringResourceForFilterAreaId(int id)
    {
        switch (id)
        {
            case R.id.action_no_filter:
                return R.string.filter_none;

            case R.id.action_filter_amministrazioni_centrali:
                return R.string.filter_amministrazioni_centrali;

            case R.id.action_filter_universita:
                return R.string.filter_uni;

            case R.id.action_filter_aziende_sanitarie:
                return R.string.filter_aziende_sanitarie;

            case R.id.action_filter_enti_pubblici_statali:
                return R.string.filter_enti_pubblici_statali;

            case R.id.action_filter_enti_locali:
                return R.string.filter_enti_locali;

            case R.id.action_filter_altri_enti:
                return R.string.filter_altri_enti;

            default:
                return 0;
        }
    }

    public static String formatDate(String dateString, String format)
    {
        DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        DateFormat dfVisualization = new SimpleDateFormat(format, Locale.ITALY);

        try
        {
            Date d = dfInsert.parse(dateString);

            return dfVisualization.format(d);

        } catch (ParseException | NullPointerException e)
        {
            return null;
        }
    }

    public static void showConnectionAlert(Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle(R.string.connection_alert_title)
                .setMessage(R.string.connection_alert_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
