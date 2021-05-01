package zaid.d.laps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mTrackingClient: TrackingClient
    private lateinit var button: Button
    private var updateHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Test button to set map to current location only works once gps/network is established
        button = findViewById<Button>(R.id.button)
        mTrackingClient = TrackingClient(this)

        button.setOnClickListener() {
            plotMarkerOnCurrentLocation()
        }
    }

    /**
    Called once the map fragment is ready
    Input: googleMap: The google map from the mapFragment
    Output: None
     */
    override fun onMapReady(googleMap: GoogleMap) {

        // Sets the global map to the created map
        mMap = googleMap

        // Gets the current position
        button.performClick()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mTrackingClient.getCurrentLocation(), 20f))

        // Updates the position regularly
        updateHandler.postDelayed(object: Runnable {
            override fun run() {
                button.performClick()
                updateHandler.postDelayed(this, 1000)
            }
        }, 0)
    }

    private fun plotMarkerOnCurrentLocation() {
        mMap.clear()

        val myLocation = mTrackingClient.getCurrentLocation()
        val circleOptions = CircleOptions().center(myLocation).radius(1.0)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 20f))
        mMap.addCircle(circleOptions)
    }

}

