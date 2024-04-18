package com.hr.navigator.customer.ui.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.databinding.ActivityStartBinding
import com.hr.navigator.customer.ui.home.DashboardActivity
import com.hr.navigator.customer.ui.login.LoginActivity
import com.hr.navigator.customer.ui.permission.AppPermissionActivity
import com.hr.navigator.customer.ui.profile.ProfileActivity
import com.hr.navigator.customer.utils.AppPermission
import com.hr.navigator.customer.utils.PrefUtil
import com.permissionx.guolindev.PermissionX


class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var appPermission: AppPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        appPermission = AppPermission(this)
        Handler(Looper.getMainLooper()).postDelayed({
            val isLoginEnabled = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_LOGIN, applicationContext)
            val isProfileFilled =
                PrefUtil.getBooleanPref(PrefUtil.PREF_IS_PROFILE_FILLED, applicationContext)
            val isGranted = isCheckPermission()
            if (isGranted) {
                if (isProfileFilled) {
                    startActivityWithFadeInAnimation(DashboardActivity.getIntent(this))
                    finish()
                } else {
                    if (isLoginEnabled) {
                        startActivityWithFadeInAnimation(ProfileActivity.getIntent(this))
                        finish()
                    } else {
                        startActivityWithFadeInAnimation(LoginActivity.getIntent(this))
                        finish()
                    }
                }
            } else {
                startActivityWithFadeInAnimation(AppPermissionActivity.getIntent(this))
                finish()
            }

        }, 2000L)
    }

    private fun isCheckPermission(): Boolean {
        var isPermissionGranted = false
        PermissionX.init(this)
            .permissions(appPermission.permissionList)
            .request { allGranted, grantedList, deniedList ->
                isPermissionGranted = allGranted
            }
        return isPermissionGranted
    }
}