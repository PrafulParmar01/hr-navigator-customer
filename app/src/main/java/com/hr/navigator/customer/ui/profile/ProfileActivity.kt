package com.hr.navigator.customer.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.getAddress
import com.hr.navigator.customer.base.extentions.hideKeyboard
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.databinding.ActivityProfileBinding
import com.hr.navigator.customer.ui.companylist.CompanyListActivity
import com.hr.navigator.customer.ui.home.DashboardActivity
import com.hr.navigator.customer.ui.location.LocationActivity
import com.hr.navigator.customer.utils.PrefUtil
import com.hr.navigator.customer.utils.UtilsMethod
import java.util.Locale


class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var strFirstName = ""
    private var strLastName = ""
    private var strDesignation = ""
    private var strEmail = ""
    private var strPhone = ""

    private var strLocation = ""
    private var strAddressLine = ""
    private var formattedLatitude = ""
    private var formattedLongitude = ""

    private var selectCompanyModel: CompanyModel? = null

    private lateinit var database: DatabaseReference

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        strPhone = PrefUtil.getStringPref(PrefUtil.PREF_PHONE_NUMBER, applicationContext)
        binding.edtPhone.text = strPhone

        binding.lToolbar.txtTitle.text = "Company Information"
        binding.lToolbar.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                insertProfileDetails()
            }
        }
        binding.txtLocation.setOnClickListener {
            intentLauncher.launch(LocationActivity.getIntent(this))
        }

        binding.txtCompany.setOnClickListener {
            companyLauncher.launch(CompanyListActivity.getIntent(this))
        }
    }


    private val companyLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val companyString = result.data?.getStringExtra("company_model") as String
                selectCompanyModel = Gson().fromJson(companyString, CompanyModel::class.java)
                selectCompanyModel.let {
                    binding.txtCompany.text = selectCompanyModel?.companyName.toString()
                }
            }
        }


    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                formattedLatitude = result.data?.getStringExtra("latitude") as String
                formattedLongitude = result.data?.getStringExtra("longitude") as String
                if (formattedLatitude.isNotEmpty() && formattedLongitude.isNotEmpty()){
                    Geocoder(applicationContext, Locale("in")).getAddress(formattedLatitude.toDouble(), formattedLongitude.toDouble()) { address: Address? ->
                        if (address != null) {
                            val shortAddress = UtilsMethod.generateShortAddress(address)
                            strAddressLine = UtilsMethod.generateAddress(address)
                            binding.txtLocation.text = shortAddress
                            binding.txtAddress.text = "Address: "+strAddressLine
                        }
                    }
                }
            }
        }



    private fun isCheckValidation(): Boolean {
        var isCheck = false
        strFirstName = binding.edtFirstName.text.toString()
        strLastName = binding.edtLastName.text.toString()
        strDesignation = binding.edtDesignation.text.toString()
        strEmail = binding.edtEmail.text.toString()
        strPhone = binding.edtPhone.text.toString()
        strLocation = binding.txtLocation.text.toString()

        if (strFirstName.isEmpty()) {
            toastShort("Please enter first name")
            isCheck = true
        } else if (strLastName.isEmpty()) {
            toastShort("Please enter first name")
            isCheck = true
        } else if (strDesignation.isEmpty()) {
            toastShort("Please enter designation")
            isCheck = true
        } else if (strEmail.isEmpty()) {
            toastShort("Please email address")
            isCheck = true
        } else if (strPhone.isEmpty()) {
            toastShort("Please enter phone number")
            isCheck = true
        } else if (selectCompanyModel == null) {
            toastShort("Please select company name")
            isCheck = true
        } else if (formattedLatitude.isEmpty() && formattedLongitude.isEmpty()) {
            toastShort("Please select location")
            isCheck = true
        }
        return isCheck
    }

    private fun insertProfileDetails() {
        progressDialogs.showProgressDialog()
        val userModel = UserModel(
            firstName = strFirstName,
            lastName = strLastName,
            designation = strDesignation,
            email = strEmail,
            phone = strPhone,
            latitude = formattedLatitude,
            longitude = formattedLongitude,
            addressLine = strAddressLine,
            type = "customer",
            companyModel = selectCompanyModel
        )

        val instance = FirebaseDatabase.getInstance()
        database = instance.reference
        database.child("Users").child(strPhone).setValue(userModel)
            .addOnSuccessListener {
                progressDialogs.dismissDialog()
                toastShort("Profile details added successfully")
                PrefUtil.putStringPref(PrefUtil.PREF_USER_MODEL, Gson().toJson(userModel),applicationContext)
                PrefUtil.putBooleanPref(PrefUtil.PREF_IS_PROFILE_FILLED, true, applicationContext)
                val intent = DashboardActivity.getIntent(this)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityWithFadeInAnimation(intent)
                finish()
            }.addOnFailureListener {
                progressDialogs.dismissDialog()
                Log.e("error : ", "===> " + it.message.toString())
                toastShort("Something went wrong. Please try again later.")
            }
    }
}