package com.hr.navigator.customer.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.hr.navigator.customer.R
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.startActivityWithFadeInAnimation
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.databinding.ActivityHomeBinding
import com.hr.navigator.customer.geofencing.GeofenceManager
import com.hr.navigator.customer.services.LocationRequestEvent
import com.hr.navigator.customer.services.LocationsService
import com.hr.navigator.customer.ui.profile.CompanyModel
import com.hr.navigator.customer.ui.profile.UserModel
import com.hr.navigator.customer.utils.AppPermission
import com.hr.navigator.customer.utils.LocationRequester
import com.hr.navigator.customer.utils.PrefUtil
import com.hr.navigator.customer.utils.UtilsMethod
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DashboardActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appPermission: AppPermission

    private lateinit var mMap: GoogleMap
    private var isZoom = false
    private lateinit var locationRequester: LocationRequester

    private lateinit var geofenceManager: GeofenceManager


    companion object {
        private const val DEFAULT_ZOOM = 12
        fun getIntent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPermission = AppPermission(this)
        locationRequester = LocationRequester(this)
        geofenceManager = GeofenceManager(this)
        getLocationPermission()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        updateGeofenceState();
        binding.checkBoxGeofence.setOnClickListener {
            val isGeofenceEnabled = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_GEOFENCE_ENABLED, this)
            if (isGeofenceEnabled) {
                PrefUtil.putBooleanPref(PrefUtil.PRF_IS_GEOFENCE_ENABLED, false, this)
                binding.checkBoxGeofence.isChecked = false
            } else {
                PrefUtil.putBooleanPref(PrefUtil.PRF_IS_GEOFENCE_ENABLED, true, this)
                binding.checkBoxGeofence.isChecked = true
            }
            syncGeofencing()
        }

        binding.btnManuallyEntry.setOnClickListener {
            showManuallyEntryDialog()
        }
    }

    private fun syncGeofencing() {
        val isGeofenceEnabled = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_GEOFENCE_ENABLED, this)
        if (isGeofenceEnabled) {
            geofenceManager.onStartGeofencing()
        } else {
            Toast.makeText(mContext, "Geofence disabled successfully", Toast.LENGTH_SHORT).show()
            geofenceManager.removeGeofencing()
        }
    }

    private fun updateGeofenceState() {
        val isGeofenceEnabled = PrefUtil.getBooleanPref(PrefUtil.PRF_IS_GEOFENCE_ENABLED, this)
        binding.checkBoxGeofence.isChecked = isGeofenceEnabled
    }

    private fun getLocationPermission() {
        PermissionX.init(this)
            .permissions(appPermission.permissionList)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String>, beforeRequest: Boolean ->
                scope.showRequestReasonDialog(
                    deniedList, "HR Navigator needs following permissions to continue", "Allow"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String> ->
                scope.showForwardToSettingsDialog(
                    deniedList, "Please allow following permissions in settings", "Allow"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    locationRequester.startLocationRequest()
                } else {
                    toastShort("Please allow permission")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onSyncLocationService()
        }
    }

    private fun onSyncLocationService() {
        if (!LocationsService.isServiceRunning(mContext)) {
            startService(LocationsService.getIntent(this))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationRequester.LOCATION_SETTINGS_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationRequester.onEnabledLocationDialog()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLocationRequestEvent(locationRequest: LocationRequestEvent) {
        if (locationRequest.isSuccess && locationRequest.location != null) {
            getDeviceLocation(locationRequest.location)
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onGeofenceReceivedRequestEvent(geofenceSendModel: GeofenceSendModel) {
        try {
            val entryTime = geofenceSendModel.enterTime
            val exitTime = geofenceSendModel.exitTime
            val companyModel = geofenceSendModel.companyModel
            val userModel = UtilsMethod.convertStringToUserModel(this)

            val geofenceEntryModel = GeofenceEntryModel(
                firstName = userModel.firstName,
                lastName = userModel.lastName,
                designation = userModel.designation,
                phone = userModel.phone,
                latitude = userModel.latitude,
                longitude = userModel.longitude,
                addressLine = userModel.addressLine,
                entryTime = entryTime,
                exitTime = exitTime,
                companyModel = companyModel
            )

            progressDialogs.showProgressDialog()
            val instance = FirebaseDatabase.getInstance()
            val database = instance.reference
            database.child("GeofenceEntry").push().setValue(geofenceEntryModel)
                .addOnSuccessListener {
                    progressDialogs.dismissDialog()
                    toastShort("Entry added successfully")
                }.addOnFailureListener {
                    progressDialogs.dismissDialog()
                    toastShort("Entry failed")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showManuallyEntryDialog() {
        val items = arrayOf("Entry", "Exit", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an entry type")
        builder.setSingleChoiceItems(items, -1) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    onManuallyEntry(true)
                }
                1 -> {
                    dialog.dismiss()
                    onManuallyEntry(false)
                }
                else -> {
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onManuallyEntry(isEntry: Boolean) {
        try {
            var entryTime = ""
            var exitTime = ""
            if (isEntry) {
                entryTime = UtilsMethod.dateFormatterCurrentDate()
            } else {
                exitTime = UtilsMethod.dateFormatterCurrentDate()
            }

            val userModel = UtilsMethod.convertStringToUserModel(this)
            val companyModel = userModel.companyModel

            val geofenceEntryModel = GeofenceEntryModel(
                firstName = userModel.firstName,
                lastName = userModel.lastName,
                designation = userModel.designation,
                phone = userModel.phone,
                latitude = userModel.latitude,
                longitude = userModel.longitude,
                addressLine = userModel.addressLine,
                entryTime = entryTime,
                exitTime = exitTime,
                companyModel = companyModel
            )

            progressDialogs.showProgressDialog()
            val instance = FirebaseDatabase.getInstance()
            val database = instance.reference
            database.child("GeofenceEntry").push().setValue(geofenceEntryModel)
                .addOnSuccessListener {
                    progressDialogs.dismissDialog()
                    toastShort("Entry added successfully")
                }.addOnFailureListener {
                    progressDialogs.dismissDialog()
                    toastShort("Entry failed")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getDeviceLocation(location: Location?) {
        try {
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                if (!isZoom) {
                    mMap.clear()
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            DEFAULT_ZOOM.toFloat()
                        )
                    )

                    isZoom = true
                    mMap.addMarker(
                        MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(latLng)
                            .title("Current location")
                    )

                    val userModel = UtilsMethod.convertStringToUserModel(this)
                    if (userModel.companyModel != null) {
                        val destLatitude = userModel.companyModel.latitude.toDouble()
                        val destLongitude = userModel.companyModel.longitude.toDouble()
                        val destination = LatLng(destLatitude, destLongitude)
                        val title =
                            userModel.companyModel.companyName + "\n" + userModel.addressLine
                        mMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                .position(destination)
                                .title(title)
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}