package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.contentprovider.ConcorsiGazzettaContentProvider;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;
import com.distesala.android_concorsi_gazzetta.utils.Helper;


/**
 * Created by damiano on 29/08/16.
 */
public class ContestCursorAdapter extends CursorAdapter
{
    public ContestCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.contest_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor)
    {
        TextView emettitoreTextView = (TextView) view.findViewById(R.id.emettitore);
        TextView titoloTextView = (TextView) view.findViewById(R.id.titolo);
        TextView expiringTextView = (TextView) view.findViewById(R.id.expiringDate);
        ImageButton favButton = (ImageButton) view.findViewById(R.id.fav_button);

        emettitoreTextView.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE)));
        titoloTextView.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO)));

        String expiringDate = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA));

        String expiringDateString = Helper.formatDate(expiringDate, "dd MMM");

        final int isFav = cursor.getInt(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE));
        favButton.setImageResource((isFav == 0) ? R.drawable.star_off : R.drawable.star_on );

        if(expiringDateString != null)
            expiringTextView.setText(expiringDateString);
        else
            expiringTextView.setText("");

        final String contestID = cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO));

        favButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_FAVORITE,  isFav ^ 1);

                context.getContentResolver().update(ConcorsiGazzettaContentProvider.CONTESTS_URI,
                        contentValues,
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO + " =?",
                        new String[]{contestID});
            }
        });
    }
}
