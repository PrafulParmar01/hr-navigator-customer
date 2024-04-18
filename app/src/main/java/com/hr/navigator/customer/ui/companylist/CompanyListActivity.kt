package com.hr.navigator.customer.ui.companylist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.addOnBackPressedDispatcher
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.databinding.ActivityCompanyListBinding
import com.hr.navigator.customer.ui.profile.CompanyModel

class CompanyListActivity : BaseActivity() {

    private lateinit var binding: ActivityCompanyListBinding
    val list = arrayListOf<CompanyModel>()

    private var selectedCompanyModel : CompanyModel?=null

    private lateinit var companyListAdapter: CompanyListAdapter

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CompanyListActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompanyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initClicks()
    }

    private fun initViews() {
        binding.layoutToolBar.txtTitle.text = "Company List"
        addOnBackPressedDispatcher {
            finish()
        }

        companyListAdapter = CompanyListAdapter(this)
        binding.recycleViewCompany.apply {
            layoutManager = LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
            adapter = companyListAdapter
        }

        companyListAdapter.onClickArrow = { companyModel ->
            this.selectedCompanyModel = companyModel
        }

        binding.btnSubmit.setOnClickListener {
            if (selectedCompanyModel!=null){
                val data = Intent()
                data.putExtra("company_model", Gson().toJson(selectedCompanyModel))
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            else{
                toastShort("Please select company name")
            }
        }

        onSyncReadData()
    }

    private fun onSyncReadData() {
        progressDialogs.showProgressDialog()
        val firebase = FirebaseDatabase.getInstance()
        val reference = firebase.reference
        reference.child("CompanyList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (x in snapshot.children) {
                        val address = x.child("address").value.toString()
                        val addressLine = x.child("addressLine").value.toString()
                        val companyId = x.child("companyId").value.toString()
                        val companyName = x.child("companyName").value.toString()
                        val email = x.child("email").value.toString()
                        val latitude = x.child("latitude").value.toString()
                        val longitude = x.child("longitude").value.toString()
                        val phone = x.child("phone").value.toString()
                        val type = x.child("type").value.toString()

                        val companyModel = CompanyModel()
                        companyModel.address = address
                        companyModel.addressLine = addressLine
                        companyModel.companyId = companyId
                        companyModel.companyName = companyName
                        companyModel.email = email
                        companyModel.latitude = latitude
                        companyModel.longitude = longitude
                        companyModel.phone = phone
                        companyModel.type = type
                        list.add(companyModel)
                        companyListAdapter.onUpdateDiaryList(list)
                        progressDialogs.dismissDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toastShort("Something went wrong. Please try again later.")
                    progressDialogs.dismissDialog()
                }
            })
    }


    private fun initClicks() {
        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

    }
}