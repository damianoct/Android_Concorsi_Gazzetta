package com.distesala.android_concorsi_gazzetta.ui;

import java.io.Serializable;

/**
 * Created by damiano on 31/08/16.
 */
public interface LoaderHandler extends Serializable
{
    public LoaderDispatcher loaderDispatcher = LoaderDispatcher.getInstance();

    void onLoadFinished();
}
