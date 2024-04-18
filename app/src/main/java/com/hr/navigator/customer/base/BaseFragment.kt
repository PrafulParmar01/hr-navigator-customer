package com.hr.navigator.customer.base

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.hr.navigator.customer.utils.JSDialogUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BaseFragment : Fragment() {

    val compositeDisposable = CompositeDisposable()

    lateinit var baseActivity: Activity
    lateinit var dialogUtils: JSDialogUtils
    var mCurrentImageUri: Uri? = null


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

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
