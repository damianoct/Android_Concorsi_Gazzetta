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

import com.distesala.android_concorsi_gazzetta.database.ConcorsiGazzetteSQLiteHelper;

/**
 * Created by damiano on 31/08/16.
 */

public class ConcorsiGazzettaContentProvider extends ContentProvider
{
    private ConcorsiGazzetteSQLiteHelper database;

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
        database = new ConcorsiGazzetteSQLiteHelper(getContext());
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
                c = db.query(ConcorsiGazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs, null, null,
                        sortOrder);

                break;
            }

            case CONTESTS:
            {
                c = db.query(ConcorsiGazzetteSQLiteHelper.ContestEntry.TABLE_NAME,
                        projection,
                        selection, selectionArgs,
                        null, null, null);
                break;
            }
        }

        //notify
        if (c != null)
            c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        SQLiteDatabase db = database.getWritableDatabase();
        db.beginTransaction();

        for(int i = 0; i < values.length; i++)
            this.insert(uri, values[i]);

        db.setTransactionSuccessful();
        db.endTransaction();

        return values.length;
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
                long id = db.insertWithOnConflict(ConcorsiGazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                returnUri = ContentUris.withAppendedId(GAZZETTE_URI, id);
                break;
            }

            case CONTESTS:
            {
                long id = db.insertWithOnConflict(ConcorsiGazzetteSQLiteHelper.ContestEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                returnUri = ContentUris.withAppendedId(CONTESTS_URI, id);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {

        SQLiteDatabase db = database.getWritableDatabase();

        int itemDeleted = 0;

        switch(sURIMatcher.match(uri))
        {
            case GAZZETTE:
            {
                itemDeleted = db.delete(ConcorsiGazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, selection, selectionArgs);
                itemDeleted += db.delete(ConcorsiGazzetteSQLiteHelper.ContestEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }

            case CONTESTS:
            {
                //Future implementazioni se necessario.
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return itemDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = database.getWritableDatabase();
        int rowAffected = 0;

        switch(sURIMatcher.match(uri))
        {
            case GAZZETTE:
            {
                rowAffected = db.update(ConcorsiGazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            case CONTESTS:
            {
                rowAffected = db.update(ConcorsiGazzetteSQLiteHelper.ContestEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowAffected;
    }
}
