package com.distesala.android_concorsi_gazzetta.ui;

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

        numberOfPublication.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION)));
        dateOfPublication.setText(cursor.getString(cursor.getColumnIndex(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION)));
    }
}
