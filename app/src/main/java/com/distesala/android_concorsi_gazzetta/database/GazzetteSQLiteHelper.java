package com.distesala.android_concorsi_gazzetta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.model.Concorso;
import com.distesala.android_concorsi_gazzetta.model.Gazzetta;

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
        public static final String COLUMN_TIPOLOGIA = "tipologia";
        public static final String COLUMN_SCADENZA = "scadenza";
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
                                                    ContestEntry.COLUMN_TIPOLOGIA + " text not null, " +
                                                    ContestEntry.COLUMN_SCADENZA + " text, " +
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

    public static ContentValues createContentValuesForGazzetta(Gazzetta g)
    {
        ContentValues gazzettaValues = new ContentValues();

        gazzettaValues.put(GazzettaEntry.COLUMN_ID_GAZZETTA, g.getIdGazzetta());
        gazzettaValues.put(GazzettaEntry.COLUMN_NUMBER_OF_PUBLICATION, g.getNumberOfPublication());
        gazzettaValues.put(GazzettaEntry.COLUMN_DATE_OF_PUBLICATION,  g.getDateOfPublication());

        return gazzettaValues;
    }

    public static ContentValues createContentValuesForContest(Concorso c)
    {
        ContentValues contestContentValues = new ContentValues();

        contestContentValues.put(ContestEntry.COLUMN_ID_CONCORSO, c.getCodiceRedazionale());
        contestContentValues.put(ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION, c.getGazzettaNumberOfPublication());
        contestContentValues.put(ContestEntry.COLUMN_TITOLO, c.getTitoloConcorso());
        contestContentValues.put(ContestEntry.COLUMN_EMETTITORE, c.getEmettitore());
        contestContentValues.put(ContestEntry.COLUMN_AREA, c.getAreaDiInteresse());
        contestContentValues.put(ContestEntry.COLUMN_TIPOLOGIA, c.getTipologia());
        contestContentValues.put(ContestEntry.COLUMN_SCADENZA, c.getScadenza());
        contestContentValues.put(ContestEntry.COLUMN_N_ARTICOLI, c.getAreaDiInteresse());

        return contestContentValues;
    }

}
