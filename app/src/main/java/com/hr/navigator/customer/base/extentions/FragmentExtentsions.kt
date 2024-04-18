@file:JvmName("FragmentExtension")
package com.hr.navigator.customer.base.extentions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


inline fun <reified T : ViewModel> Fragment.getViewModel(
        crossinline factory: () -> T
): T {
    return createViewModel(factory)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.createViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProvider(this, vmFactory)[T::class.java]
}


inline fun <reified T : ViewModel> Fragment.getViewModel(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, vmFactory)[T::class.java]
}
