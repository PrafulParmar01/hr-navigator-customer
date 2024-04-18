package com.hr.navigator.customer.base.extentions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import timber.log.Timber

fun Context?.hideKeyboard() {
    if (this == null) {
        return
    }
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    var activity: Activity? = null
    if (this is Activity) {
        activity = this
    } else if (this is ContextWrapper) {
        val parentContext = this.baseContext
        if (parentContext is Activity) {
            activity = parentContext
        }
    }

    if (activity == null) {
        Timber.w("Try to hide keyboard but context type is incorrect %s", this.javaClass.simpleName)
        return
    }

    if (activity.currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    } else {
        Timber.w("Try to hide keyboard but there is no current focus view")
    }
}
