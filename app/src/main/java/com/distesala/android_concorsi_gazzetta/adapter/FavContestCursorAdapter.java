package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
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
 * Created by damiano on 29/08/16.
 */

public class FavContestCursorAdapter extends CursorAdapter
{
    public FavContestCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.contest_item_star, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        TextView emettitore = (TextView) view.findViewById(R.id.emettitore);
        TextView titolo = (TextView) view.findViewById(R.id.titolo);
        TextView expiringDate = (TextView) view.findViewById(R.id.expiringDate);

        ImageButton rubbishButton = (ImageButton) view.findViewById(R.id.fav_button);

        emettitore.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE)));
        titolo.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO)));

        String dateOfPublication = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA));

        DateFormat dfInsert = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        DateFormat dfVisualization = new SimpleDateFormat("dd MMM", Locale.ITALY);

        try
        {
            Date d = dfInsert.parse(dateOfPublication);

            expiringDate.setText(dfVisualization.format(d));

        }
        catch (ParseException | NullPointerException e)
        {
            expiringDate.setVisibility(View.GONE);
        }


        final int isFav = cursor.getInt(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE));

        rubbishButton.setImageResource(R.drawable.ic_delete_gray_24px);


        final String contestID = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO));

        rubbishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE,  isFav ^ 1);
                Log.i("MELINTA", String.valueOf(isFav ^ 1));

                context.getContentResolver().update(ConcorsiGazzettaContentProvider.CONTESTS_URI,
                        contentValues,
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO + " =?",
                        new String[]{contestID});


            }
        });
    }
}
