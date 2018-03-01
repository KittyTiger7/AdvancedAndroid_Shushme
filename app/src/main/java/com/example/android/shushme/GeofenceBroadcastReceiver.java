package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by avigagel on 01/03/2018.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /***
     * handles the broadcast message sent when the geofence transition is triggered (enter/exit/dwell).
     * this is running on the main thread. so, make sure to start an AsyncTask (Jessica Lynn, the
     * instructor said that you should start an AsyncTask for anything that takes longer than 10 seconds
     * to run.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called");
    }
}
