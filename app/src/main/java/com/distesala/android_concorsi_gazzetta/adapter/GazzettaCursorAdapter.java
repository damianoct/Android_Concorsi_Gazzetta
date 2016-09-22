package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marco on 22/08/16.
 */
public class GazzettaCursorAdapter extends CursorAdapter
{
    public GazzettaCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.gazzetta_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView numberOfPublication = (TextView) view.findViewById(R.id.numberOfPublication);
        TextView dateOfPublication = (TextView) view.findViewById(R.id.dateOfPublication);
        TextView nContest = (TextView) view.findViewById(R.id.nConcorsi);
        TextView nExpiring = (TextView) view.findViewById(R.id.nExpiring);

        String numberOfPup = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION));

        String whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + "=?";
        String[] whereArgs = new String[] { numberOfPup };

        int numContest = context.getContentResolver().query(ConcorsiGazzettaContentProvider.CONTESTS_URI, null, whereClause, whereArgs, null).getCount();

        numberOfPublication.setText(context.getString(R.string.pubblicazione) + numberOfPup);
        nContest.setText(String.valueOf(numContest));


        String key = context.getString(R.string.key_scadenza_threshold);
        String threshold = String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0));

        whereClause = GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA +
                        " <= date('now', '+" + threshold + " days') AND " +
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA + " >= date('now') " + "AND " +
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " =?";

        int numExp = context.getContentResolver().query(ConcorsiGazzettaContentProvider.CONTESTS_URI, null, whereClause, whereArgs, null).getCount();
        nExpiring.setText(String.valueOf(numExp));

        //YYYY-MM-DD
        String date = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION));
        DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        DateFormat dfVisualization = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALY);

        try
        {
            Date d = dfInsert.parse(date);

            dateOfPublication.setText(dfVisualization.format(d));


        } catch (ParseException e)
        {
            dateOfPublication.setText("1 gennaio 1970"); // :-)
        }

    }
}
