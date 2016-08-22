package com.distesala.android_concorsi_gazzetta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.distesala.android_concorsi_gazzetta.model.Gazzetta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 21/08/16.
 */
public class GazzetteDataSource
{
    private SQLiteDatabase database;
    private GazzetteSQLiteHelper dbHelper;
    private String[] allColumns = { GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA,
                                    GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION,
                                    GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION};

    public GazzetteDataSource(Context context)
    {
        dbHelper = new GazzetteSQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public void insertGazzetta(Gazzetta gazzetta)
    {
        //TODO: momentaneamente inserisco una singola gazzetta
        ContentValues values = new ContentValues();
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA, gazzetta.getIdGazzetta());
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, gazzetta.getNumberOfPublication());
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  gazzetta.getDateOfPublication());

        database.insertWithOnConflict(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertGazzette(Gazzetta[] gazzette)
    {
        database.beginTransaction();
        ContentValues values = new ContentValues();

        for (int i = 0; i < gazzette.length; i++)
        {
            values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA, gazzette[i].getIdGazzetta());
            values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, gazzette[i].getNumberOfPublication());
            values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  gazzette[i].getDateOfPublication());

            database.insertWithOnConflict(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public Cursor getGazzetteCursor()
    {
        //ordino per data di pubblicazione
        return database.query(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, allColumns, null, null, null, null, GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA + " DESC");
    }

    public List<Gazzetta> getAllGazzette()
    {
        List<Gazzetta> gazzette = new ArrayList<Gazzetta>();

        Cursor cursor = getGazzetteCursor();


        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Gazzetta gazzetta = cursorToGazzetta(cursor);
            gazzette.add(gazzetta);
            cursor.moveToNext();
        }

        cursor.close();

        return gazzette;
    }

    private Gazzetta cursorToGazzetta(Cursor cursor) {
        Gazzetta gazzetta = new Gazzetta();
        gazzetta.setIdGazzetta(cursor.getInt(0));
        gazzetta.setNumberOfPublication(cursor.getString(1));
        gazzetta.setDateOfPublication(cursor.getString(2));
        return gazzetta;
    }

}
