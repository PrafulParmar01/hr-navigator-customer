@file:JvmName("ActivityExtension")

package com.hr.navigator.customer.base.extentions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


fun Activity.startActivityForResultWithFadeInAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.startActivityWithFadeInAnimation(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}


fun Activity.startNewActivityWithFadeInAnimation(intent: Intent) {
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Activity.endActivityWithFadeOutAnimation() {
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

fun Context.toastShort(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(
        crossinline factory: () -> T
): T {
    return createViewModel(factory)
}


@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> AppCompatActivity.createViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProvider(this, vmFactory)[T::class.java]
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModelFromFactory(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, vmFactory)[T::class.java]
}



fun AppCompatActivity.addOnBackPressedDispatcher(onBackPressed: () -> Unit = { finish() }) {
    onBackPressedDispatcher.addCallback(
        this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed.invoke()
            }
        }
    )
}

@Suppress("DEPRECATION")
fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
    address: (Address?) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
        return
    }

    try {
        address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
    } catch (e: Exception) {
        address(null)
    }
}