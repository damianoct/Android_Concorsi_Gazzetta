package com.distesala.android_concorsi_gazzetta.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Marco on 21/08/16.
 */
public class GazzetteSQLiteHelper extends SQLiteOpenHelper
{
    public abstract class GazzettaEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "gazzette";

        public static final String COLUMN_ID_GAZZETTA = _ID;
        public static final String COLUMN_NUMBER_OF_PUBLICATION = "numberOfPublication";
        public static final String COLUMN_DATE_OF_PUBLICATION = "dateOfPublication";
    }

    public abstract class ContestEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "concorsi";

        public static final String COLUMN_ID_CONCORSO = _ID;
        public static final String COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION = "gazzettaNumberOfPublication";
        public static final String COLUMN_EMETTITORE = "emettitore";
        public static final String COLUMN_AREA = "areaDiInteresse";
        public static final String COLUMN_TITOLO = "titoloConcorso";
        public static final String COLUMN_N_ARTICOLI = "numeroArticoli";

    }

    private static final String DATABASE_NAME = "gazzette.db";

    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_GAZZETTE = "create table " + GazzettaEntry.TABLE_NAME + "( " +
                                                    GazzettaEntry.COLUMN_ID_GAZZETTA + " integer primary key, " +
                                                    GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " text not null, " +
                                                    GazzettaEntry.COLUMN_DATE_OF_PUBLICATION + " text not null);";

    private static final String CREATE_CONCORSI = "create table " + ContestEntry.TABLE_NAME + "( " +
                                                    ContestEntry.COLUMN_ID_CONCORSO + " text primary key, " +
                                                    ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + " text not null, " +
                                                    ContestEntry.COLUMN_EMETTITORE + " text not null, " +
                                                    ContestEntry.COLUMN_AREA + " text not null, " +
                                                    ContestEntry.COLUMN_TITOLO + " text not null, " +
                                                    ContestEntry.COLUMN_N_ARTICOLI + " text not null);";

    public GazzetteSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_GAZZETTE);
        db.execSQL(CREATE_CONCORSI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(GazzetteSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GazzettaEntry.TABLE_NAME);
        onCreate(db);
    }
}
