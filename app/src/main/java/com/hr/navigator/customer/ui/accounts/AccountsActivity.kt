package com.hr.navigator.customer.ui.accounts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.addOnBackPressedDispatcher
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.databinding.ActivityAccountsBinding
import com.hr.navigator.customer.dialogs.LogoutDialog
import com.hr.navigator.customer.ui.login.LoginActivity
import com.hr.navigator.customer.utils.PrefUtil


class AccountsActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountsBinding

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AccountsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        binding.layoutToolBar.txtTitle.text = "Accounts"
        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        addOnBackPressedDispatcher {
            finish()
        }

        binding.btnProfileInformation.setOnClickListener {
            startActivityWithFadeInAnimation(ProfileInformationActivity.getIntent(applicationContext))
        }


        binding.btnLogout.setOnClickListener {
            val logout = LogoutDialog()
            logout.openDialog(this)
            logout.setOnDialogListener(object :LogoutDialog.OnClickListener{
                override fun onClick() {
                    onLogoutClicked()
                }
            })
        }
    }

    private fun onLogoutClicked() {
        PrefUtil.deletePrefData(applicationContext)
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}