package com.hr.navigator.customer.ui.permission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.databinding.ActivityAppPermissionAcvtivityBinding
import com.hr.navigator.customer.ui.login.LoginActivity
import com.hr.navigator.customer.utils.AppPermission
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope

class AppPermissionActivity : BaseActivity() {

    private lateinit var binding: ActivityAppPermissionAcvtivityBinding
    private lateinit var appPermission: AppPermission

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AppPermissionActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppPermissionAcvtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPermission = AppPermission(this)

        binding.btnContinue.setOnClickListener(View.OnClickListener { v: View? ->
            PermissionX.init(this)
                .permissions(appPermission!!.permissionList)
                .explainReasonBeforeRequest()
                .onExplainRequestReason { scope: ExplainScope, deniedList: List<String>, beforeRequest: Boolean ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "HR Navigator needs following permissions to continue",
                        "Allow"
                    )
                }
                .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "Please allow following permissions in settings",
                        "Allow"
                    )
                }
                .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                    if (allGranted) {
                        startActivityWithFadeInAnimation(LoginActivity.getIntent(applicationContext))
                    } else {
                        Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }
}