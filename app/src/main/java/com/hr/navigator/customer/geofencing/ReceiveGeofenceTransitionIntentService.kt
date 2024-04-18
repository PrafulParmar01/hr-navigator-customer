package com.hr.navigator.customer.geofencing

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.utils.UtilsMethod


abstract class ReceiveGeofenceTransitionIntentService :
    IntentService("ReceiveGeofenceTransitionIntentService") {

    private lateinit var mContext: Context

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent!!)
        if (event != null) {
            if (event.hasError()) {
                onError(mContext, event.errorCode)
            } else {
                val transition = event.geofenceTransition
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                    val geoList = UtilsMethod.convertStringToUserModel(mContext)
                    Log.e("geoList: ", "===> ${geoList}")

                    val triggeringGeoFences = event.triggeringGeofences
                    val geofenceTransitionDetails = getGeofenceTransitionDetails(
                        triggeringGeoFences!!,
                        geoList.companyModel
                    )

                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        onEnteredGeofences(mContext, geoList.companyModel!!)
                    } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                        onExitedGeofences(mContext, geoList.companyModel!!)
                    }
                }
            }
        }
    }

    protected abstract fun onEnteredGeofences(context: Context, geofenceIds: CompanyModel)
    protected abstract fun onExitedGeofences(context: Context, geofenceIds: CompanyModel)
    protected abstract fun onError(context: Context, errorCode: Int)


    private fun getGeofenceTransitionDetails(
        triggeringGeofences: MutableList<Geofence>,
        companyModel: CompanyModel?
    ): ArrayList<CompanyModel> {

        val idsList = ArrayList<CompanyModel>()
        for (geofence in triggeringGeofences) {
            val latLngDetails = getLatLngDetails(geofence.latitude, geofence.longitude, companyModel)
            if(latLngDetails!=null) {
                idsList.add(latLngDetails)
            }
        }
        Log.e("idsList size: ", "===> ${idsList.size}")

        return idsList
    }

    private fun getLatLngDetails(
        latitude: Double,
        longitude: Double,
        geoList: CompanyModel?
    ): CompanyModel? {
        var dataModel: CompanyModel? = null
        //for (data in geoList) {
            if (geoList?.latitude?.toDouble() == latitude && geoList?.longitude?.toDouble() == longitude) {
                dataModel = geoList
                //dataModel = data.state + ": "+data.title + " LatLng: "+ LatLng(data.latitude.toDouble(),data.longitude.toDouble())
            }
        //}
        return dataModel
    }
}