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



fun Context?.showKeyboard() {
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
        Timber.w("Try to show keyboard but context type is incorrect %s", this.javaClass.simpleName)
        return
    }

    val currentFocusView = activity.currentFocus
    currentFocusView?.postDelayed({ inputMethodManager.showSoftInput(currentFocusView, 0) }, 100)
            ?: Timber.w("Try to show keyboard but there is no current focus view")
}

fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5).toInt()
}


fun Context.pickColor(resourceId: Int): Int {
    return ContextCompat.getColor(this, resourceId)
}

fun Context.pickColorString(resourceId: Int): String {
    val colorInt = ContextCompat.getColor(this, resourceId)
    return java.lang.String.format("#%06X", 0xFFFFFF and colorInt)

}



fun Fragment.pickColor(resourceId: Int): Int {
    return ContextCompat.getColor(this.requireContext(), resourceId)
}

fun Fragment.pickColorString(resourceId: Int): String {
    val colorInt = ContextCompat.getColor(this.requireContext(), resourceId)
    return java.lang.String.format("#%06X", 0xFFFFFF and colorInt)

}

fun Context?.getDeviceHeight(): Int? {
    val metrics = this?.resources?.displayMetrics
    return metrics?.heightPixels
}

fun Context?.getDeviceWidth(): Int? {
    val metrics = this?.resources?.displayMetrics
    return metrics?.widthPixels
}