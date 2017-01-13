package com.distesala.android_concorsi_gazzetta.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by damiano on 11/01/17.
 */

public class GazzettaSwipeRefreshLayout extends SwipeRefreshLayout
{
    public GazzettaSwipeRefreshLayout(Context context)
    {
        super(context);
    }

    public GazzettaSwipeRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public interface OnChildScrollUpCallback
    {
        boolean canChildScrollUp();
    }

    public OnChildScrollUpCallback onChildScrollUpCallback;

    public void setOnChildScrollUpCallback(OnChildScrollUpCallback callback)
    {
        this.onChildScrollUpCallback = callback;
    }

    @Override
    public boolean canChildScrollUp()
    {
        return onChildScrollUpCallback.canChildScrollUp();
    }
}
