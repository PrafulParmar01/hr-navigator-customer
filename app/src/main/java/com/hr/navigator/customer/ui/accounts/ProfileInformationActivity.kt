package com.hr.navigator.customer.ui.accounts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.getAddress
import com.hr.navigator.customer.base.extentions.hideKeyboard
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.databinding.ActivityProfileInformationBinding
import com.hr.navigator.customer.ui.companylist.CompanyListActivity
import com.hr.navigator.customer.ui.location.LocationActivity
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.ui.profile.UserModel
import com.hr.navigator.customer.utils.PrefUtil
import com.hr.navigator.customer.utils.UtilsMethod
import java.util.Locale


class ProfileInformationActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileInformationBinding
    private var strFirstName = ""
    private var strLastName = ""
    private var strDesignation = ""
    private var strEmail = ""
    private var strPhone = ""
    private var strLocation = ""
    private var strAddressLine = ""

    private var formattedLatitude = ""
    private var formattedLongitude = ""

    private lateinit var getUserModel: UserModel
    private var selectCompanyModel: CompanyModel? = null

    private lateinit var database: DatabaseReference

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileInformationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        getUserModel = UtilsMethod.convertStringToUserModel(applicationContext)

        binding.layoutToolBar.txtTitle.text = "Profile Information"
        binding.layoutToolBar.btnEdit.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE

        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        binding.layoutToolBar.btnEdit.setOnClickListener {
            setEditEnabled()
            binding.btnSave.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            if (!isCheckValidation()) {
                hideKeyboard()
                updateProfileDetails()
            }
        }
        binding.txtLocation.setOnClickListener {
            intentLauncher.launch(LocationActivity.getIntent(this))
        }

        binding.txtCompany.setOnClickListener {
            companyLauncher.launch(CompanyListActivity.getIntent(this))
        }

        setDefaultData()
        setEditDisabled()
    }

    private fun setEditDisabled() {
        binding.edtFirstName.isEnabled = false
        binding.edtLastName.isEnabled = false
        binding.edtDesignation.isEnabled = false
        binding.txtCompany.isEnabled = false
        binding.txtLocation.isEnabled = false
    }

    private fun setEditEnabled() {
        binding.edtFirstName.isEnabled = true
        binding.edtLastName.isEnabled = true
        binding.edtDesignation.isEnabled = true
        binding.txtCompany.isEnabled = true
        binding.txtLocation.isEnabled = true
    }


    private fun setDefaultData() {
        strFirstName = getUserModel.firstName
        strLastName = getUserModel.lastName
        strDesignation = getUserModel.designation
        strEmail = getUserModel.email
        strPhone = getUserModel.phone

        formattedLatitude = getUserModel.latitude
        formattedLongitude = getUserModel.longitude
        strAddressLine = getUserModel.addressLine
        selectCompanyModel = getUserModel.companyModel

        binding.edtFirstName.setText(strFirstName)
        binding.edtLastName.setText(strLastName)
        binding.edtDesignation.setText(strDesignation)
        binding.txtCompany.text = getUserModel.companyModel?.companyName.toString()
        Geocoder(applicationContext, Locale.getDefault()).getAddress(formattedLatitude.toDouble(), formattedLongitude.toDouble()) { address: Address? ->
            if (address != null) {
                val shortAddress = UtilsMethod.generateShortAddress(address)
                binding.txtLocation.text = shortAddress
                binding.txtAddress.text = "Address: "+strAddressLine
            }
        }
    }


    private val companyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val companyString = result.data?.getStringExtra("company_model") as String
                selectCompanyModel = Gson().fromJson(companyString, CompanyModel::class.java)
                selectCompanyModel.let {
                    binding.txtCompany.text = selectCompanyModel?.companyName.toString()
                }
            }
        }


    private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        } else if (selectCompanyModel == null) {
            toastShort("Please select company name")
            isCheck = true
        } else if (strLocation.isEmpty()) {
            toastShort("Please select location")
            isCheck = true
        }
        return isCheck
    }

    private fun updateProfileDetails() {
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
                toastShort("Profile updated successfully")
                PrefUtil.putStringPref(PrefUtil.PREF_USER_MODEL, Gson().toJson(userModel),applicationContext)
                finish()
            }.addOnFailureListener {
                progressDialogs.dismissDialog()
                Log.e("error : ", "===> " + it.message.toString())
                toastShort("Something went wrong. Please try again later.")
            }
    }
}