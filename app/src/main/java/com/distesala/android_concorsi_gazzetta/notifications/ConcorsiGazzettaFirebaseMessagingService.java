package com.distesala.android_concorsi_gazzetta.notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by damiano on 08/05/17.
 */

public class ConcorsiGazzettaFirebaseMessagingService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
