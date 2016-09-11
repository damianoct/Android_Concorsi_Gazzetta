package com.distesala.android_concorsi_gazzetta.ui.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by damiano on 11/09/16.
 */

/**
 * ho creato la classe e non l'interfaccia in modo tale che
 * gli HostSearchablesFragment debbano mantenere degli oggetti Fragment (dato che questa classe estente Fragment)
 * e non è possibile aggiungere a una tab un'oggetto che non sia un Fragment.
 * Nel caso di un interfaccia potevo creare qualcosa che la implementava ma che non è un Fragment.
 */
public abstract class SearchableFragment extends Fragment
{
    //TODO Gestire i cursor Loader...

    public SearchableFragment() {}

    protected abstract void performSearch(String querySearch);

}
