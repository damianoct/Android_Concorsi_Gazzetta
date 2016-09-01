package com.distesala.android_concorsi_gazzetta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    private String[] allGazzettaColumns =   {   GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA,
                                                GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION,
                                                GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION
                                            };

    private String[] allContestColumns =    {   GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_AREA,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI,
                                                GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO
                                            };

    public GazzetteDataSource(Context context)
    {
        dbHelper = new GazzetteSQLiteHelper(context);
    }

    private Gazzetta cursorToGazzetta(Cursor cursor)
    {
        Gazzetta gazzetta = new Gazzetta();
        gazzetta.setIdGazzetta(cursor.getInt(0));
        gazzetta.setNumberOfPublication(cursor.getString(1));
        gazzetta.setDateOfPublication(cursor.getString(2));
        return gazzetta;
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
        ContentValues values = new ContentValues();
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA, gazzetta.getIdGazzetta());
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, gazzetta.getNumberOfPublication());
        values.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  gazzetta.getDateOfPublication());

        database.insertWithOnConflict(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void insertGazzette(Gazzetta[] gazzette)
    {
        // TODO: Inserire un comportamento per cui ci sia un limite massimo di record nel Database

        database.beginTransaction();
        ContentValues gazzetteValues = new ContentValues();
        ContentValues contestValues = new ContentValues();

        for (int i = 0; i < gazzette.length; i++)
        {
            gazzetteValues.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA, gazzette[i].getIdGazzetta());
            gazzetteValues.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, gazzette[i].getNumberOfPublication());
            gazzetteValues.put(GazzetteSQLiteHelper.GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  gazzette[i].getDateOfPublication());

            database.beginTransaction();

            //add contests for each gazzetta
            for(int j = 0; j < gazzette[i].getConcorsi().size(); j++)
            {
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_ID_CONCORSO, gazzette[i].getConcorsi().get(j).getCodiceRedazionale());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION, gazzette[i].getNumberOfPublication());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_TITOLO, gazzette[i].getConcorsi().get(j).getTitoloConcorso());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_EMETTITORE, gazzette[i].getConcorsi().get(j).getEmettitore());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_AREA, gazzette[i].getConcorsi().get(j).getAreaDiInteresse());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_TIPOLOGIA, gazzette[i].getConcorsi().get(j).getTipologia());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_SCADENZA, gazzette[i].getConcorsi().get(j).getScadenza());
                contestValues.put(GazzetteSQLiteHelper.ContestEntry.COLUMN_N_ARTICOLI, gazzette[i].getConcorsi().get(j).getAreaDiInteresse());

                database.insertWithOnConflict(GazzetteSQLiteHelper.ContestEntry.TABLE_NAME, null, contestValues, SQLiteDatabase.CONFLICT_IGNORE);
            }

            database.setTransactionSuccessful();
            database.endTransaction();

            database.insertWithOnConflict(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, gazzetteValues, SQLiteDatabase.CONFLICT_IGNORE);

        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public Cursor getGazzetteCursor()
    {
        //sort by date of publication
        return database.query(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME,
                allGazzettaColumns, null, null, null, null,
                            GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA + " DESC");
    }

    public Cursor getContestsForGazzetta(String numberOfPublication)
    {
        return database.query(GazzetteSQLiteHelper.ContestEntry.TABLE_NAME,
                allContestColumns,
                GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + "=?", new String[] {numberOfPublication}, //selection
                null, null, null);

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

    public boolean gazzettaExists(Gazzetta gazzetta)
    {
        Cursor cursor = database.query(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME,
                        new String[]{allGazzettaColumns[0]},
                        GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA + " = " + gazzetta.getIdGazzetta(),
                        null, null, null, null);

        return cursor.moveToFirst();
    }

}
