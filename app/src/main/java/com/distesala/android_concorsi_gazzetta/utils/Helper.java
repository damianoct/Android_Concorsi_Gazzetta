package com.distesala.android_concorsi_gazzetta.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;

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
    private static SparseIntArray FILTERS;
    static
    {
        FILTERS = new SparseIntArray();
        FILTERS.put(R.id.action_no_filter, R.string.filter_none);
        FILTERS.put(R.id.action_filter_amministrazioni_centrali, R.string.filter_amministrazioni_centrali);
        FILTERS.put(R.id.action_filter_universita, R.string.filter_uni);
        FILTERS.put(R.id.action_filter_aziende_sanitarie, R.string.filter_aziende_sanitarie);
        FILTERS.put(R.id.action_filter_enti_pubblici_statali, R.string.filter_enti_pubblici_statali);
        FILTERS.put(R.id.action_filter_enti_locali, R.string.filter_enti_locali);
        FILTERS.put(R.id.action_filter_altri_enti, R.string.filter_altri_enti);
    }

    public static int getStringResourceForFilterAreaId(int id)
    {
        return FILTERS.get(id);
    }

    public static void selectItem(Menu menu, int id)
    {
        for(int i = 0; i < FILTERS.size(); i++)
        {
            int identifier = FILTERS.keyAt(i);
            MenuItem item = menu.findItem(R.id.menu_item_filtering).getSubMenu().findItem(identifier);
            item.setChecked(identifier == id);
        }
    }

    public static String formatDate(String dateString, String format)
    {
        DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        DateFormat dfVisualization = new SimpleDateFormat(format, Locale.ITALY);

        if(dateString != null)
        {
            try
            {
                Date d = dfInsert.parse(dateString);

                return dfVisualization.format(d);

            } catch (ParseException e)
            {
                return null;
            }
        }
        else
            return null;
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
