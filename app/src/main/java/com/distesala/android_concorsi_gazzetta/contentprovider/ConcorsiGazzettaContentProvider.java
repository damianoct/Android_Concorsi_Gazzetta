package com.distesala.android_concorsi_gazzetta.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

/**
 * Created by damiano on 31/08/16.
 */

public class ConcorsiGazzettaContentProvider extends ContentProvider
{
    private GazzetteSQLiteHelper database;

    // used for the UriMacher
    private static final int GAZZETTE = 10;
    private static final int GAZZETTA_ID = 11;
    private static final int CONTESTS = 12;
    private static final int CONTESTS_ID = 13;

    private static final String AUTHORITY = "com.distesala.android_concorsi_gazzetta.contentprovider";

    private static final String GAZZETTE_PATH = "gazzette";

    private static final String CONTESTS_PATH = "contests";

    public static final Uri GAZZETTE_URI = Uri.parse("content://" + AUTHORITY
            + "/" + GAZZETTE_PATH);

    public static final Uri CONTESTS_URI = Uri.parse("content://" + AUTHORITY
            + "/" + CONTESTS_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static
    {
        sURIMatcher.addURI(AUTHORITY, GAZZETTE_PATH, GAZZETTE);
        sURIMatcher.addURI(AUTHORITY, GAZZETTE_PATH + "/#", GAZZETTA_ID);
        sURIMatcher.addURI(AUTHORITY, CONTESTS_PATH, CONTESTS);
        sURIMatcher.addURI(AUTHORITY, CONTESTS_PATH + "/#", CONTESTS_ID);
    }

    @Override
    public boolean onCreate()
    {
        database = new GazzetteSQLiteHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor c = null;

        SQLiteDatabase db = database.getWritableDatabase();

        switch(sURIMatcher.match(uri))
        {
            case GAZZETTE:
            {
                c = db.query(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs, null, null,
                        GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA + " DESC"); //order by

                Log.i("loader content provider", "cursor -> " + String.valueOf(c.getCount()));
                break;
            }

            case CONTESTS:
            {
                c = db.query(GazzetteSQLiteHelper.ContestEntry.TABLE_NAME,
                        projection,
                        GazzetteSQLiteHelper.ContestEntry.COLUMN_GAZZETTA_NUMBER_OF_PUBLICATION + "=?", selectionArgs, //selection
                        null, null, null);
                break;
            }
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {

        SQLiteDatabase db = database.getWritableDatabase();

        Uri returnUri = null;

        switch(sURIMatcher.match(uri))
        {
            case GAZZETTE:
            {
                long id = db.insertWithOnConflict(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = ContentUris.withAppendedId(GAZZETTE_URI, id);
                Log.d("provider INSERT", returnUri.toString());
                break;
            }

            case CONTESTS:
            {
                long id = db.insertWithOnConflict(GazzetteSQLiteHelper.ContestEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = ContentUris.withAppendedId(CONTESTS_URI, id);
                Log.d("provider INSERT", returnUri.toString());
                break;
            }
        }

        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }
}
