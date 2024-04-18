package com.hr.navigator.customer.geofencing

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.hr.navigator.customer.ui.home.GeofenceSendModel
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.utils.NotificationUtils
import com.hr.navigator.customer.utils.UtilsMethod
import org.greenrobot.eventbus.EventBus

class GeofencingReceiver : ReceiveGeofenceTransitionIntentService() {
    override fun onEnteredGeofences(context: Context, companyModel: CompanyModel) {
        Log.e("GeofencingReceiver", "onEnteredGeofence: ${companyModel}")
        val entryTime = UtilsMethod.dateFormatterCurrentDate()
        EventBus.getDefault().postSticky(GeofenceSendModel(entryTime,"",companyModel))
        val message = "You have entered the location of "+ companyModel.companyName
        NotificationUtils(context).startMessageNotification(message)
    }

    override fun onExitedGeofences(context: Context, companyModel: CompanyModel) {
        Log.e("GeofencingReceiver", "onExitedGeofence: ${companyModel}")
        val exitTime = UtilsMethod.dateFormatterCurrentDate()
        EventBus.getDefault().postSticky(GeofenceSendModel("",exitTime,companyModel))
        val message = "You have exited from the location of "+ companyModel.companyName
        NotificationUtils(context).startMessageNotification(message)
    }

    override fun onError(context: Context, errorCode: Int) {
        Log.e("GeofencingReceiver", "Error: $errorCode")
        val message = "There was an issue receiving the enter or exit geofence signal"
        NotificationUtils(context).startMessageNotification(message)
    }
}