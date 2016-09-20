package com.distesala.android_concorsi_gazzetta.utils;

import com.distesala.android_concorsi_gazzetta.R;

/**
 * Created by damiano on 20/09/16.
 */
public class Helper
{
    public static int getStringResourceForFilterAreaId(int id)
    {
        switch (id)
        {
            case R.id.action_no_filter:
                return R.string.filter_none;

            case R.id.action_filter_amministrazioni_centrali:
                return R.string.filter_amministrazioni_centrali;

            case R.id.action_filter_universita:
                return R.string.filter_uni;

            case R.id.action_filter_aziende_sanitarie:
                return R.string.filter_aziende_sanitarie;

            case R.id.action_filter_enti_pubblici_statali:
                return R.string.filter_enti_pubblici_statali;

            case R.id.action_filter_enti_locali:
                return R.string.filter_enti_locali;

            case R.id.action_filter_altri_enti:
                return R.string.filter_altri_enti;

            default:
                return 0;
        }
    }
}
