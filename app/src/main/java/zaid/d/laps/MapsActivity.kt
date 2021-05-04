package zaid.d.laps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var isMapReady = false
    private lateinit var mTrackingClient: TrackingClient
    private lateinit var mMarkerManager: MarkerManager
    private lateinit var button: Button
    private var updateHandler = Handler()

    /**
    Called once the activity starts
    Input: savedInstanceState: App's previous data from activity
    Output: None
     */
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
        waitForMap()

        button.setOnClickListener() {
            mMarkerManager.plotMarkerOnCurrentLocation()
        }
    }

    /**
    Called once the map fragment is ready
    Input: googleMap: The google map from the mapFragment
    Output: None
     */
    override fun onMapReady(googleMap: GoogleMap) {
        // Sets the global map to the created map
        isMapReady = true
        mMap = googleMap

        // Sets up the marker on the map
        mMarkerManager = MarkerManager(this, mMap)
        mMarkerManager.setMarker()

        // Gets the current position
        button.performClick()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mTrackingClient.getLatLong(), 20f))

        // Updates the position regularly
        updateHandler.postDelayed(object: Runnable {
            override fun run() {
                mMarkerManager.plotMarkerOnCurrentLocation()
                updateHandler.postDelayed(this, 1000)
            }
        }, 0)
    }

    /**
    Repeatedly loops until the map is ready
    Input: None
    Output: None
     */
    private fun waitForMap() {
        val handler = Handler()
        handler.post(object: Runnable {
            override fun run() {
                if (!isMapReady) handler.postDelayed(this, 100)
            }
        })
    }

}

