package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;

/**
 * Created by avigagel on 01/03/2018.
 */

public class Geofencing {

    private static final String TAG = Geofencing.class.getSimpleName();

    private Context mContext;
    private GoogleApiClient mApiClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    private int GEOFENCE_TIMEOUT = 24*60*60; // 24 hour (in seconds)
    private float GEOFENCE_RADIUS = 50;      // 50 meters.


    public Geofencing(Context context, GoogleApiClient apiClient) {
        mContext = context;
        mApiClient = apiClient;
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
    }

    public void registerAllGeofences() {
        // Check that Api Client is connected, and that the list has Geofences in it
        if (mApiClient == null || !mApiClient.isConnected() ||
                mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }

        try {
            LocationServices.getGeofencingClient(mContext).addGeofences(getGeofencingRequest(), mGeofencePendingIntent);
            // In the line above, I wrote different code than the one in the class, because one of the classes
            // .. used there was deprecated. For that, I saw something about adding a addOnFailureListener.
            // ... if ever use this for production, need to check this more thoroughly.
        } catch (SecurityException securityException) {
            // Catch exception generated if app doesn't have ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }


    public void unRegisterAllGeofences() {
        if (mApiClient == null || !mApiClient.isConnected()) return;

        try {
            LocationServices.getGeofencingClient(mContext).removeGeofences(mGeofencePendingIntent);
            // Same comment here as in the message above
        } catch (SecurityException securityException) {
            // Catch exception generated if app doesn't have ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }


    // A Unique ID is to be used for every GeoFence, so we'll use the Unique Places-IDs from previously
    public void updateGeofencesList(PlaceBuffer places) {
        mGeofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place: places) {
            String placeID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLong = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    // for production quality app, use job scheduler to re-register geofences every day.
                    .setCircularRegion(placeLat, placeLong, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            mGeofenceList.add(geofence);
        }
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // The bellow line is for instances when the device is inside any of the geofences that we're
        // .. about to register. the INITIAL_TRIGGER_ENTER means that it'll trigger an entry right away.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // re-use the pending intent if we already have it
        if (mGeofencePendingIntent != null) return mGeofencePendingIntent;

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }



}
