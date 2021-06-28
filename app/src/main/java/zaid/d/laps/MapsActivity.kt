package zaid.d.laps

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var isMapReady = false
    private lateinit var mMarkerManager: MarkerManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var startLocation: Location
    private lateinit var button: Button
    private var updateHandler = Handler()
    private var clicked = false

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
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val extras  = intent.extras
        startLocation = extras?.get("startLocation") as Location

        // Test button to set map to current location only works once gps/network is established
        button = findViewById<Button>(R.id.button)

        waitForMap()
        button.setOnClickListener() {
            if (clicked) {
                clicked = false
                supportFragmentManager.popBackStack()
            }
            else {
                clicked = true

                // Saves the points to a file
                val points = mMarkerManager.getPoints()
                PointsFile.savePoints(this, "testing",ConversionsLocation.optimizeCords(points))

                val listRouteFragment = ListRouteFragment()
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flRouteList, listRouteFragment)
                    addToBackStack("close")
                    commit()
                }
            }
        }
    }

    /**
    Called once the map fragment is ready
    Input: googleMap: The google map from the mapFragment
    Output: None
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        // Sets the global map to the created map
        isMapReady = true
        mMap = googleMap
        mMap.isBuildingsEnabled = false
        mMap.isMyLocationEnabled = true



        // Sets up the marker on the map
        mMarkerManager = MarkerManager(this, mMap)
        mMarkerManager.setMarker(ConversionsLocation.getCords(startLocation))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ConversionsLocation.getCords(startLocation), ConstantsZoom.MAIN_ZOOM))

        // Keeps track of the position on the previous update
        var oldPosition = startLocation

        // Updates the position regularly
        updateHandler.postDelayed(object: Runnable {
            override fun run() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    oldPosition = mMarkerManager.interpolateMarker(oldPosition, location!!, true)
                }
                updateHandler.postDelayed(this, ConstantsTime.DELAY_TIME)
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
                if (!isMapReady) handler.postDelayed(this, ConstantsTime.DELAY_SHORT)
            }
        })
    }

}

