package com.hr.navigator.customer.geofencing

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.Geofence
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.utils.UtilsMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID


class GeofenceManager(private val mContext: Context) : GeofencingRegistererCallbacks {

    private val uniqueGeofenceLists: MutableList<CompanyModel> = mutableListOf()
    private var geofencingRegisterer: GeofencingRegisterer?=null

    init {
        geofencingRegisterer = GeofencingRegisterer(mContext)
        geofencingRegisterer?.setGeofencingCallback(this)
    }

     fun onStartGeofencing() {
        manageGeofenceData()
    }

    private fun manageGeofenceData() {
        val userModel = UtilsMethod.convertStringToUserModel(mContext)
        if (userModel.companyModel!=null) {
            uniqueGeofenceLists.clear()
            uniqueGeofenceLists.add(userModel.companyModel)
        }
        CoroutineScope(Dispatchers.IO).launch {
            async { registerGeofence() }.await()
        }
    }


    private fun registerGeofence() {
        val geofenceLists: MutableList<Geofence> = mutableListOf()
        if (uniqueGeofenceLists.isNotEmpty()) {
            for (data in uniqueGeofenceLists) {
                val geofence: Geofence = Geofence.Builder()
                    .setCircularRegion(
                        data.latitude.toDouble(),
                        data.longitude.toDouble(),
                        30f
                    )
                    .setRequestId(UUID.randomUUID().toString())
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build()
                geofenceLists.add(geofence)
            }

            geofencingRegisterer?.removeGeofenceIds()
            geofencingRegisterer?.registerGeofences(geofenceLists)
            Log.e("GeofenceService: ", "===> Done $uniqueGeofenceLists")
        }
    }

    fun removeGeofencing(){
        geofencingRegisterer?.removeGeofenceIds()
    }


    override fun onApiClientConnected() {
        Log.e("onApiClientConnected", "===> ok")
    }

    override fun onApiClientSuspended() {
        Log.e("onApiClientSuspended", "===> ok")
    }

    override fun onApiClientConnectionFailed(connectionResult: ConnectionResult?) {
        Log.e("onApiClientFailed", "===> ok")
    }

    override fun onGeofenceRegisteredSuccessful(message: String?) {
        Toast.makeText(mContext, "Geofence registered successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onGeofenceRegisteredFailed(message: String?) {
        Toast.makeText(mContext, "Oops! Geofence registration failed", Toast.LENGTH_SHORT).show()
    }
}