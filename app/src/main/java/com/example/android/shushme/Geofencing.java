package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;

/**
 * Created by avigagel on 01/03/2018.
 */

public class Geofencing {

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
}
