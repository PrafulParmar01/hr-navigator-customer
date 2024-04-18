package com.hr.navigator.customer.base

import android.app.Activity
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hr.navigator.customer.utils.JSDialogUtils
import com.hr.navigator.customer.receivers.NetworkConnectivityReceiver
import com.hr.navigator.customer.receivers.NetworkRefreshEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus


abstract class BaseActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()
    lateinit var mContext: Activity
    lateinit var progressDialogs: JSDialogUtils

    private var networkReceiver: NetworkConnectivityReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        progressDialogs = JSDialogUtils(this)

        networkReceiver = NetworkConnectivityReceiver()
        networkReceiver?.setNetworkListener(object : NetworkConnectivityReceiver.NetworkListener {
            override fun onNetworkEnabled(isConnected: Boolean) {
                if (isConnected) {
                    EventBus.getDefault().postSticky(NetworkRefreshEvent(true))
                } else {
                    EventBus.getDefault().postSticky(NetworkRefreshEvent(false))
                }
            }
        })
        this.registerReceiver(
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

        override fun onDestroy() {
            compositeDisposable.clear()
            super.onDestroy()
            if (networkReceiver != null) {
                this.unregisterReceiver(networkReceiver)
            }
        }

        fun Disposable.autoDispose() {
            compositeDisposable.add(this)
        }

    fun replaceFragment(fragment: Fragment?, mContainerId: Int) {
        try {
            if (fragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(mContainerId, fragment)
                transaction.commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}