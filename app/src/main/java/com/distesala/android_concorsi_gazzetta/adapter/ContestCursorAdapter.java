package com.distesala.android_concorsi_gazzetta.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.distesala.android_concorsi_gazzetta.R;
import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

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
        return LayoutInflater.from(context).inflate(R.layout.contest_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView emettitore = (TextView) view.findViewById(R.id.emettitore);
        TextView titolo = (TextView) view.findViewById(R.id.titolo);

        emettitore.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE)));
        titolo.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO)));
    }
}
