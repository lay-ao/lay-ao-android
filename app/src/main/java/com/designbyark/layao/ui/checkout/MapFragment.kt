package com.designbyark.layao.ui.checkout

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.designbyark.layao.R
import com.designbyark.layao.common.isGPSEnabled
import com.designbyark.layao.helper.LocationHelper
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private var currentLocation: LatLng? = null
    private var location: LocationHelper? = null
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        location =
            LocationHelper(
                requireActivity(),
                object :
                    LocationHelper.CustomLocationListener {
                    override fun locationResponse(locationResult: LocationResult) {
                        currentLocation = LatLng(
                            locationResult.lastLocation.latitude,
                            locationResult.lastLocation.longitude
                        )
                    }
                })

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setHomeButtonEnabled(true)
//            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
//        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.mCurrentLocation.setOnClickListener {

            if (currentLocation == null) {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch location... Try Again",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (isGPSEnabled(requireContext())) {
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
                mMap?.addMarker(markerStyle(currentLocation!!))

                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geoCoder.getFromLocation(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude,
                    1
                )

                val address = addresses[0].getAddressLine(0)
                if (address != null) {
                    view.mAddress.text = address
                } else {
                    view.mAddress.text = "Fetching Address... Try Again"
                }


            } else {
                AlertDialog.Builder(requireContext())
                    .setMessage("To continue, turn on device location, which Google's location service")
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }
    }

    private fun markerStyle(latLng: LatLng): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        return markerOptions
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.clear()
        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        val lahore = LatLng(31.5204, 74.3587)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(lahore, 10f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        location?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onStart() {
        super.onStart()
        location?.initializeLocation()
    }

    override fun onStop() {
        super.onStop()
        location?.stopUpdateLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }


}