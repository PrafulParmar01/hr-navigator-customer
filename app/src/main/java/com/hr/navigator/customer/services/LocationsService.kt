package com.hr.navigator.customer.services

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.hr.navigator.customer.base.locations.BaseLocationHelper
import com.hr.navigator.customer.utils.PrefUtil
import com.hr.navigator.customer.utils.NotificationUtils
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class LocationsService : Service(), BaseLocationHelper.NewLocationListener {

    private lateinit var mContext: Context
    private lateinit var notificationUtils: NotificationUtils
    private lateinit var baseLocationHelper: BaseLocationHelper

    companion object {
        var CURRENT_LATITUDE = 0.0
        var CURRENT_LONGITUDE = 0.0

        fun getIntent(context: Context): Intent {
            return Intent(context, LocationsService::class.java)
        }

        @Suppress("deprecation")
        fun isServiceRunning(mContext: Context): Boolean {
            val manager = mContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationsService::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        notificationUtils = NotificationUtils(this)
        notificationUtils.startForegroundLocationNotification()
        Timber.e("LocationService:onCreate : ===> Done")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.e("LocationService:onStartCommand : ===> Done")
        onBackGroundDiscovery()
        return START_STICKY
    }


    private fun onBackGroundDiscovery() {
        baseLocationHelper = BaseLocationHelper(mContext)
        baseLocationHelper.initLocation()
        baseLocationHelper.connectLocation()
        baseLocationHelper.setOnNewLocationListener(this)
    }


    private fun removeObserver() {
        baseLocationHelper.disconnectLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObserver()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.v("LocationService:onTaskRemoved : ===> Done")
    }

    override fun onNewLocation(locationResult: Location?, available: Boolean) {
        CURRENT_LATITUDE = locationResult?.latitude as Double
        CURRENT_LONGITUDE = locationResult.longitude
        PrefUtil.putStringPref(PrefUtil.PREF_LATITUDE, CURRENT_LATITUDE.toString(), mContext)
        PrefUtil.putStringPref(PrefUtil.PREF_LONGITUDE, CURRENT_LONGITUDE.toString(), mContext)
        Timber.e("CURRENT_LATITUDE : ===> $CURRENT_LATITUDE")
        Timber.e("CURRENT_LONGITUDE : ===> $CURRENT_LONGITUDE")
        EventBus.getDefault().postSticky(LocationRequestEvent(true, locationResult))
    }
}