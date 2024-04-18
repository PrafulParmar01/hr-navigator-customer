package com.hr.navigator.customer.ui.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hr.navigator.customer.R
import com.hr.navigator.customer.base.BaseActivity
import com.hr.navigator.customer.base.extentions.addOnBackPressedDispatcher
import com.hr.navigator.customer.base.extentions.toastShort
import com.hr.navigator.customer.base.locations.BaseLocationHelper
import com.hr.navigator.customer.databinding.ActivityTrackingBinding
import com.hr.navigator.customer.utils.AppPermission
import com.hr.navigator.customer.utils.LocationRequester
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import java.text.DecimalFormat


class LocationActivity : BaseActivity(), OnMapReadyCallback, BaseLocationHelper.NewLocationListener {

    private lateinit var binding: ActivityTrackingBinding
    private lateinit var mMap: GoogleMap
    private var isZoom = false

    private var baseLocationHelper: BaseLocationHelper? = null

    private var mLatitude = ""
    private var mLongitude = ""


    private lateinit var locationRequester: LocationRequester
    private lateinit var appPermission: AppPermission


    companion object {
        private const val DEFAULT_ZOOM = 12

        fun getIntent(context: Context): Intent {
            return Intent(context, LocationActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        appPermission = AppPermission(this)
        locationRequester = LocationRequester(this)
        getLocationPermission()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.layoutToolBar.txtTitle.text = "Select Location"
        binding.layoutToolBar.btnBack.setOnClickListener {
            finish()
        }

        addOnBackPressedDispatcher {
            finish()
        }

        binding.btnSelect.setOnClickListener {
            if (mLatitude.isNotEmpty() && mLongitude.isNotEmpty()) {
                val data = Intent()
                data.putExtra("latitude", mLatitude)
                data.putExtra("longitude", mLongitude)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
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
            updateLocationUI()
        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }


    private fun updateLocationUI() {
        try {
            baseLocationHelper = BaseLocationHelper(this)
            baseLocationHelper?.initLocation()
            baseLocationHelper?.connectLocation()
            baseLocationHelper?.setOnNewLocationListener(this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    override fun onNewLocation(locationResult: Location?, available: Boolean) {
        getDeviceLocation(locationResult)
    }

    private fun getDeviceLocation(location: Location?) {
        try {
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                mLatitude = latLng.latitude.toString()
                mLongitude = latLng.longitude.toString()

                if (!isZoom) {
                    mMap.clear()
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            DEFAULT_ZOOM.toFloat()
                        )
                    )
                    isZoom = true
                    mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title("Current location"))
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (baseLocationHelper != null) {
            baseLocationHelper?.disconnectLocation()
        }
    }
}