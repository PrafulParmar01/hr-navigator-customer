package com.hr.navigator.customer.geofencing;

import com.google.android.gms.common.ConnectionResult;

public interface GeofencingRegistererCallbacks {
    void onApiClientConnected();
    void onApiClientSuspended();
    void onApiClientConnectionFailed(ConnectionResult connectionResult);
    void onGeofenceRegisteredSuccessful(String message);
    void onGeofenceRegisteredFailed(String message);
}