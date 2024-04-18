package com.hr.navigator.customer.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.hr.navigator.customer.utils.JSDialogUtils

open class BaseFragment : Fragment() {


    lateinit var baseActivity: Activity
    lateinit var dialogUtils: JSDialogUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogUtils = JSDialogUtils(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            baseActivity = (context as Activity)
        }
    }
}
