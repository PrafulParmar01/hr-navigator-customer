package com.hr.navigator.customer.utils

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.hr.navigator.customer.R
import com.hr.navigator.customer.utils.ApiConstant.FASTEST_UPDATE_DELAY_MILLIS
import com.hr.navigator.customer.utils.ApiConstant.FASTEST_UPDATE_INTERVAL_MILLIS
import timber.log.Timber


class LocationRequester(val mContext: Activity) {

    companion object{
         const val LOCATION_SETTINGS_REQUEST = 199
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, FASTEST_UPDATE_INTERVAL_MILLIS)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MILLIS)
            .setMaxUpdateDelayMillis(FASTEST_UPDATE_DELAY_MILLIS)
            .build()
    }


    fun startLocationRequest(){
        setEnabledLocationRequest(mContext)
    }

    private fun setEnabledLocationRequest(mContext: Activity) {
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest())
        settingsBuilder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(mContext)
            .checkLocationSettings(settingsBuilder.build())
        result.addOnSuccessListener {
            Timber.e("LocationRequest: ===> SUCCESS")
        }

        result.addOnFailureListener { e ->
            when ((e as ApiException).statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    val rae = e as ResolvableApiException
                    rae.startResolutionForResult(mContext, LOCATION_SETTINGS_REQUEST)
                } catch (sie: IntentSender.SendIntentException) {
                    Timber.e("LocationRequest: ===> Unable to execute request")
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Timber.e("LocationRequest: ===>  Location settings are inadequate, and cannot be fixed here. Fix in Settings")
                }
            }
        }
        result.addOnCanceledListener {
            Timber.e("LocationRequest: ===> onCancelled")
        }
    }


    fun onEnabledLocationDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.resources.getString(R.string.enable_gps))
        builder.setMessage(mContext.resources.getString(R.string.enable_gps_msg))
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.label_settings) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }

        builder.setNegativeButton(R.string.no_close_app) { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}