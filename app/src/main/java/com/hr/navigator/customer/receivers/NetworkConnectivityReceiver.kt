package com.hr.navigator.customer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(isInternetAvailable(context)){
            if(mNetworkListener!=null){
                mNetworkListener?.onNetworkEnabled(true)
            }
        }
        else{
            if(mNetworkListener!=null){
                mNetworkListener?.onNetworkEnabled(false)
            }
        }
    }

    @Suppress("DEPRECATION")
     fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }


    interface NetworkListener {
        fun onNetworkEnabled(isConnected: Boolean)
    }

    private var mNetworkListener: NetworkListener? = null

    fun setNetworkListener(networkListener: NetworkListener) {
        this.mNetworkListener = networkListener
    }
}