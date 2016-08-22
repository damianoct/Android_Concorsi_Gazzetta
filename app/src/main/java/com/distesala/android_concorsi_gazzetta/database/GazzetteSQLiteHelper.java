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

    private static final String DATABASE_NAME = "gazzette.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_GAZZETTE = "create table " + GazzettaEntry.TABLE_NAME + "( " +
                                                    GazzettaEntry.COLUMN_ID_GAZZETTA + " integer primary key, " +
                                                    GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION + " text not null, " +
                                                    GazzettaEntry.COLUMN_DATE_OF_PUBLICATION + " text not null);";

    public GazzetteSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_GAZZETTE);
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
