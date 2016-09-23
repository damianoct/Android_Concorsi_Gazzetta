package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by damiano on 11/09/16.
 */

public abstract class SearchableFragment extends Fragment
{
    //ogni SearchableFragment possiede un Bundle che utilizza per indirizzare il loader.
    protected Bundle queryBundle;

    protected abstract void performSearch(String querySearch);

}
