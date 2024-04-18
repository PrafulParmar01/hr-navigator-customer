package com.hr.navigator.customer.geofencing

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.hr.navigator.customer.ui.home.GeofenceSendModel
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.utils.UtilsMethod
import org.greenrobot.eventbus.EventBus

class GeofencingReceiver : ReceiveGeofenceTransitionIntentService() {
    override fun onEnteredGeofences(context: Context, companyModel: CompanyModel) {
        Log.e("GeofencingReceiver", "onEnteredGeofence: ${companyModel}")
        val entryTime = UtilsMethod.dateFormatterCurrentDate()
        EventBus.getDefault().postSticky(GeofenceSendModel(entryTime,"",companyModel))
    }

    override fun onExitedGeofences(context: Context, companyModel: CompanyModel) {
        Log.e("GeofencingReceiver", "onExitedGeofence: ${companyModel}")
        val exitTime = UtilsMethod.dateFormatterCurrentDate()
        EventBus.getDefault().postSticky(GeofenceSendModel("",exitTime,companyModel))
    }

    override fun onError(context: Context, errorCode: Int) {
        Log.e("GeofencingReceiver", "Error: $errorCode")
        Toast.makeText(context, "GeofencingEvent Error: $errorCode", Toast.LENGTH_SHORT).show()
    }
}