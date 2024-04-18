package com.hr.navigator.customer.base.locations

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.hr.navigator.customer.utils.ApiConstant.FASTEST_UPDATE_DELAY_MILLIS
import com.hr.navigator.customer.utils.ApiConstant.FASTEST_UPDATE_INTERVAL_MILLIS

class BaseLocationHelper(private var mContext: Context) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var mListener: NewLocationListener? = null


    private var mGoogleApiAvailability: GoogleApiAvailability? = null
    private var mUsingGms = false

    private var mNetworkLocationListener: LocationListener? = null
    private var mGpsLocationListener: LocationListener? = null

    private var mCurrentBestLocation: Location? = null

    private val tag = "BaseLocationHelper"

    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    var isConnected = false


    private lateinit var mLocationManager: LocationManager
    private lateinit var windowManager: WindowManager
    private lateinit var defaultDisplay: Display
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10.0f


    fun initLocation() {
        mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        defaultDisplay = windowManager.defaultDisplay

        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        mGoogleApiAvailability = GoogleApiAvailability.getInstance()
        //locationRequest = LocationRequest()
        determineIfUsingGms()

        //locationRequest.interval = fastestUpdateIntervalMillis
        //locationRequest.fastestInterval = fastestUpdateIntervalMillis
        //locationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, FASTEST_UPDATE_INTERVAL_MILLIS)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MILLIS)
            .setMaxUpdateDelayMillis(FASTEST_UPDATE_DELAY_MILLIS)
            .build()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)

        mNetworkLocationListener = createLocationListener()
        mGpsLocationListener = createLocationListener()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult.lastLocation != null) {
                    mCurrentBestLocation = locationResult.lastLocation
                    newLocationUpdated(mCurrentBestLocation, "from latest api", true)
                } else {
                    Log.e(tag, "Location missing in callback.")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun connectLocation() {
        if (isUsingGms() && isGPSEnabled) {
            //used latest api
            Log.e("connectLocation", "using latest api")
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        Log.e("addOnSuccessListener", "done")
                    } else {
                        Log.e("addOnSuccessListener", "failed")
                        connectOldApi()
                    }
                }
            } catch (unlikely: SecurityException) {
                Log.e(tag, "Lost location permissions. Couldn't remove updates. $unlikely")
            }
        } else {
            //used older api
            Log.e("connectLocation", "using old api")
            connectOldApi()
        }
    }


    private fun newLocationUpdated(lastLocation: Location?, fromApi: String, available: Boolean) {
        mCurrentBestLocation = lastLocation
        isConnected = true
        mListener?.onNewLocation(lastLocation, available)
    }


    @SuppressLint("MissingPermission")
    private fun connectOldApi() {
        val lastKnownGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastKnownNetworkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        var bestLastKnownLocation = mCurrentBestLocation
        if (lastKnownGpsLocation != null && LocationUtil.isBetterLocation(lastKnownGpsLocation, bestLastKnownLocation)) {
            bestLastKnownLocation = lastKnownGpsLocation
        }
        if (lastKnownNetworkLocation != null && LocationUtil.isBetterLocation(lastKnownNetworkLocation, bestLastKnownLocation)) {
            bestLastKnownLocation = lastKnownNetworkLocation
        }

        mCurrentBestLocation = bestLastKnownLocation
        val gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.e(tag, "gpsEnabled old API: $gpsEnabled")
        Log.e(tag, "networkEnabled old API: $networkEnabled")

        if (gpsEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_UPDATE_INTERVAL_MILLIS, MIN_DISTANCE_CHANGE_FOR_UPDATES, mGpsLocationListener!!)
        }

        if (networkEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_UPDATE_INTERVAL_MILLIS, MIN_DISTANCE_CHANGE_FOR_UPDATES, mNetworkLocationListener!!)
        }

        if (!gpsEnabled && !networkEnabled) {
            Log.e(tag, "Received last known location via old API: $mCurrentBestLocation")
            newLocationUpdated(mCurrentBestLocation, "from no api", false)
        }
    }


    @SuppressLint("MissingPermission")
    fun disconnectLocation() {
        if (isUsingGms()) {
            //used latest api
            stopTrackingLocation()
        } else {
            //used older api
            mLocationManager.removeUpdates(mGpsLocationListener!!)
            mLocationManager.removeUpdates(mNetworkLocationListener!!)
        }
    }


    private fun createLocationListener(): LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.i(tag, "onLocationChanged via old API: $location")
                if (LocationUtil.isBetterLocation(location, mCurrentBestLocation)) {
                    mCurrentBestLocation = location
                    //mCurrentBestLocation?.bearing = mBearing
                    newLocationUpdated(mCurrentBestLocation, "from older api", true)
                }
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
    }


    private fun stopTrackingLocation() {
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(tag, "Location Callback removed.")
                } else {
                    Log.e(tag, "Failed to remove Location Callback.")
                }
            }
            removeTask.addOnSuccessListener {
                Log.e(tag, "success to remove Location Callback.")
            }

            removeTask.addOnFailureListener {
                Log.e(tag, "Failed to remove Location Callback.")
            }

        } catch (unlikely: SecurityException) {
            Log.e(tag, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }


    private fun isUsingGms(): Boolean {
        return mUsingGms
    }

    private fun determineIfUsingGms() {
        val statusCode = mGoogleApiAvailability?.isGooglePlayServicesAvailable(mContext)
        if (statusCode == ConnectionResult.SUCCESS || statusCode == ConnectionResult.SERVICE_UPDATING) {
            mUsingGms = true
        }
    }

    interface NewLocationListener {
        fun onNewLocation(locationResult: Location?, available: Boolean)
    }

    fun setOnNewLocationListener(newLocationListener: NewLocationListener) {
        this.mListener = newLocationListener
    }
}