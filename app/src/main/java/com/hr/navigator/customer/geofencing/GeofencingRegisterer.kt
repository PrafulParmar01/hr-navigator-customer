package com.hr.navigator.customer.geofencing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices

class GeofencingRegisterer(private val mContext: Context) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var geofencesToAdd: List<Geofence>? = null
    private val geofencesIds: MutableList<String> = ArrayList()
    private var mGeofencePendingIntent: PendingIntent? = null
    private var mCallback: GeofencingRegistererCallbacks? = null
    val TAG = this.javaClass.name

    fun setGeofencingCallback(callback: GeofencingRegistererCallbacks?) {
        mCallback = callback
    }

    fun registerGeofences(geofences: List<Geofence>?) {
        geofencesToAdd = geofences
        geofencesIds.clear()
        for (data in geofencesToAdd!!) {
            geofencesIds.add(data.requestId)
        }
        mGoogleApiClient = GoogleApiClient.Builder(mContext)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        mGoogleApiClient?.connect()
    }

    fun removeGeofenceIds() {
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            val result = LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient!!, mGeofencePendingIntent!!
            )
            result.setResultCallback { status: Status ->
                if (status.isSuccess) {
                    Log.e("removeGeofenceIds", "onSuccess: " + status.statusMessage.toString())
                } else {
                    Log.e("removeGeofenceIds", "onFailure: " + status.statusMessage.toString())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        if (mCallback != null) {
            mCallback?.onApiClientConnected()
        }
        if (mGoogleApiClient!!.isConnected && mGoogleApiClient != null) {
            mGeofencePendingIntent = requestPendingIntent()
            val result = LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient!!, geofencesToAdd!!, mGeofencePendingIntent!!
            )
            result.setResultCallback { status: Status ->
                if (status.isSuccess) {
                    if (mCallback != null) {
                        mCallback?.onGeofenceRegisteredSuccessful(status.statusMessage.toString())
                    }
                } else if (status.hasResolution()) {
                    mCallback?.onGeofenceRegisteredFailed(status.statusMessage.toString())
                } else {
                    mCallback?.onGeofenceRegisteredFailed(status.statusMessage.toString())
                }
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {
        if (mCallback != null) {
            mCallback!!.onApiClientSuspended()
        }
        Log.e(TAG, "onConnectionSuspended: $i")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (mCallback != null) {
            mCallback!!.onApiClientConnectionFailed(connectionResult)
        }
        Log.e(TAG, "onConnectionFailed: " + connectionResult.errorCode.toString())
    }

    private fun requestPendingIntent(): PendingIntent {
        return createRequestPendingIntent()
    }

    private fun createRequestPendingIntent(): PendingIntent {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent!!
        } else {
            val intent = Intent(mContext, GeofencingReceiver::class.java)
            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            return pendingIntent
        }
    }
}