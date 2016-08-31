package com.distesala.android_concorsi_gazzetta.database;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by Marco on 22/08/16.
 */
public class CursorEnvelope implements Serializable
{
    private Cursor cursor;

    public CursorEnvelope(Cursor cursor)
    {
        this.cursor = cursor;
    }

    public Cursor getCursor()
    {
        return cursor;
    }
}
