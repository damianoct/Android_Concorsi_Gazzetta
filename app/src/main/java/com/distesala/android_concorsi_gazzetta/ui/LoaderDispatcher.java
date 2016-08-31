package com.distesala.android_concorsi_gazzetta.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.distesala.android_concorsi_gazzetta.database.GazzetteSQLiteHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by damiano on 31/08/16.
 */

public class LoaderDispatcher implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int CONTEST_FOR_GAZZETTE_LOADER = 1;


    public static LoaderDispatcher instance;

    public static LoaderDispatcher getInstance()
    {
        return (instance != null) ? instance : new LoaderDispatcher();
    }

    private LoaderDispatcher() { }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        LoaderHandler client = (LoaderHandler) args.getSerializable("client");
        assert client != null;

        switch (id)
        {
            case CONTEST_FOR_GAZZETTE_LOADER:
            {
                /*return new CursorLoader(SampleListActivity.this, CONTENT_URI,
                        PROJECTION, null, null, null);
                database.query(GazzetteSQLiteHelper.GazzettaEntry.TABLE_NAME,
                        allGazzettaColumns, null, null, null, null,
                        GazzetteSQLiteHelper.GazzettaEntry.COLUMN_ID_GAZZETTA + " DESC");
                break;
                */
            }
        }


        client.onLoadFinished();
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
}
