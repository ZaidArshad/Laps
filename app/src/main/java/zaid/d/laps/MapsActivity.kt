package zaid.d.laps

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var isMapReady = false
    private lateinit var mMarkerManager: MarkerManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var startLocation: Location
    private lateinit var mainButton: Button

    lateinit var startButton: Button
    lateinit var finishButton: Button
    private lateinit var chronometer: Chronometer
    var chronometerRunning = false

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
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val extras  = intent.extras
        startLocation = extras?.get("startLocation") as Location

        // Test button to set map to current location only works once gps/network is established
        mainButton = findViewById(R.id.mainButton)
        waitForMap()

        // Sets the start button and timer
        startButton = findViewById(R.id.startButton)
        finishButton = findViewById(R.id.finishButton)
        chronometer = findViewById(R.id.chronometer)

        // Changes the orientation of the main button
        supportFragmentManager.addOnBackStackChangedListener {
            if (mainButton.rotation == 0F) mainButton.rotation = 180F
            else mainButton.rotation = 0F
        }

        // When the user wants to start running
        startButton.setOnClickListener() {
            DrawingManagement.setDrawing(this, true) // Cursor leaves trail
            fadeOut(startButton)
            fadeOut(mainButton)
            fadeIn(chronometer)
            fadeIn(finishButton)

            chronometer.base = SystemClock.elapsedRealtime()
        }

        // When the user wants to finish running
        finishButton.setOnClickListener() {
            DrawingManagement.setDrawing(this, false) // Cursor doesn't leaves trail
            fadeIn(mainButton)
            fadeOut(chronometer)
            fadeOut(finishButton)
        }

        // Main Button
        mainButton.setOnClickListener() {

            // Opens the list of routes
            if (supportFragmentManager.backStackEntryCount == 0) {

                // Passes the points into the fragment
                val listRouteFragment = ListRouteFragment()
                val bundle = Bundle()
                bundle.putSerializable("points", mMarkerManager.getPoints())
                listRouteFragment.arguments = bundle

                // Animation for the list opening
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.enter_from_bottom,
                        R.anim.exit_from_bottom,
                        R.anim.enter_from_bottom,
                        R.anim.exit_from_bottom
                    )
                    replace(R.id.flRouteList, listRouteFragment)
                    addToBackStack("open")
                    commit()
                }
            } else {
                supportFragmentManager.popBackStack()
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
        DrawingManagement.setDrawing(this, false)
        mMarkerManager = MarkerManager(this, mMap)
        mMarkerManager.setMarker(ConversionsLocation.getCords(startLocation))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ConversionsLocation.getCords(startLocation), ConstantsZoom.MAIN_ZOOM))

        // Keeps track of the position on the previous update
        var oldPosition = startLocation

        // Updates the position regularly
        updateHandler.postDelayed(object: Runnable {
            override fun run() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    oldPosition = mMarkerManager.interpolateMarker(oldPosition, location!!, DrawingManagement.getDrawing(this@MapsActivity))
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

    /**
    Fades the given view out
    Input: View object to fade out
     */
    private fun fadeOut(v: View) {
        v.animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        v.animate()
        v.visibility = View.INVISIBLE
    }

    /**
    Fades the given view in
    Input: View object to fade in
     */
    fun fadeIn(v: View) {
        v.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        v.animate()
        v.visibility = View.VISIBLE
    }

}

