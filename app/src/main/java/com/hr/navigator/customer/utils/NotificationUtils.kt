package com.hr.navigator.customer.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hr.navigator.customer.R
import com.hr.navigator.customer.services.LocationsService
import com.hr.navigator.customer.ui.home.DashboardActivity


class NotificationUtils(private val mContext: Context) {

    private val notificationManager: NotificationManager?
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var notification: Notification? = null
    private val mResources: Resources
    private var locationsService: LocationsService? = null
    private var channelLocationId = ""
    private var channelMessageId = ""

    init {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mResources = mContext.resources
        channelLocationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createLocationNotificationChannel(notificationManager) else ""
        channelMessageId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createMessageNotificationChannel(notificationManager) else ""

        if (mContext is LocationsService) {
            locationsService = mContext
        }
    }


    fun startForegroundLocationNotification() {
        notificationBuilder = NotificationCompat.Builder(mContext, channelLocationId)
        notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_logo_user)
            .setContentTitle("Location Service")
            .setContentText("Service is running...")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentIntent(launchIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (locationsService!=null) {
            locationsService?.startForeground(1000, notification)
        }
    }


    fun startMessageNotification(message:String) {
        notificationBuilder = NotificationCompat.Builder(mContext, channelMessageId)
        notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_logo_user)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentIntent(launchIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        notificationManager?.notify(5000, notification)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createLocationNotificationChannel(notificationManager: NotificationManager?): String {
        val channelId = "hr_navigator_user_location_service_channel_id"
        val channelName = "hr navigator user location service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager!!.createNotificationChannel(channel)
        return channelId
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMessageNotificationChannel(notificationManager: NotificationManager?): String {
        val channelId = "hr_navigator_user_message_service_channel_id"
        val channelName = "hr navigator user message service"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager!!.createNotificationChannel(channel)
        return channelId
    }




    @SuppressLint("UnspecifiedImmutableFlag")
    private fun launchIntent(): PendingIntent {
        val resultIntent = Intent(mContext, DashboardActivity::class.java)
        val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return contentPendingIntent
    }
}