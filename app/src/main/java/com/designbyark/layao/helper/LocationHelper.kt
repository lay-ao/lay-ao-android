package com.designbyark.layao.helper

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.designbyark.layao.util.REQUEST_CODE_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationHelper(var activity: Activity, CustomLocationListener: CustomLocationListener) {

    private val permissionFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationRequest: LocationRequest? = null
    private var callbabck: LocationCallback? = null

    init {
        fusedLocationClient = FusedLocationProviderClient(activity.applicationContext)

        inicializeLocationRequest()
        callbabck = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                CustomLocationListener.locationResponse(locationResult!!)
            }
        }
    }

    private fun inicializeLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest?.interval = 50000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validatePermissionsLocation(): Boolean {
        val fineLocationAvailable = ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            permissionFineLocation
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationAvailable = ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            permissionCoarseLocation
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationAvailable && coarseLocationAvailable
    }

    private fun requestPermissions() {
        val contextProvider =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionFineLocation)

        if (contextProvider) {
            Toast.makeText(
                activity.applicationContext,
                "Permission is required to obtain location",
                Toast.LENGTH_SHORT
            ).show()
        }
        permissionRequest()
    }

    private fun permissionRequest() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permissionFineLocation, permissionCoarseLocation),
            REQUEST_CODE_LOCATION
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(
                        activity.applicationContext,
                        "You did not give permissions to get location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun stopUpdateLocation() {
        this.fusedLocationClient?.removeLocationUpdates(callbabck)
    }

    fun initializeLocation() {
        if (validatePermissionsLocation()) {
            getLocation()
        } else {
            requestPermissions()
        }
    }

    private fun getLocation() {
        validatePermissionsLocation()
        fusedLocationClient?.requestLocationUpdates(locationRequest, callbabck, null)
    }

    interface CustomLocationListener {
        fun locationResponse(locationResult: LocationResult)
    }
}