package com.distesala.android_concorsi_gazzetta.ui.fragment;

/**
 * Created by damiano on 28/08/16.
 */
public interface FragmentListener
{
    void onSegueTransaction();
    void onHomeTransaction();
    void onFragmentDisplayed(String fragmentTag);
    void expandAppBar();
}
