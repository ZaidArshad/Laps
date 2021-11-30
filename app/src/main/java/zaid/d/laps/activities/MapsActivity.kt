package zaid.d.laps.activities

import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.android.synthetic.main.activity_maps.*
import zaid.d.laps.*
import zaid.d.laps.R
import zaid.d.laps.model.MarkerManager
import zaid.d.laps.objects.*
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mMap: GoogleMap
    private var isMapReady = false
    lateinit var mMarkerManager: MarkerManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var startLocation: Location

    private lateinit var mainButton: Button
    lateinit var startButton: Button
    lateinit var finishButton: Button
    lateinit var deleteButton: Button
    lateinit var newBestTimePrompt: TextView
    lateinit var newBestTimeValue: TextView

    var listOpened = false
    var isRecordingNewRoute = false
    var isCameraMoving = true
    var recordedTime: Long = 0
    lateinit var currentRouteFile: String

    private lateinit var chronometer: Chronometer

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

        setViews()
        waitForMap()
        setButtons()
    }

    private fun setButtons() {
        // Changes the orientation of the main button
        supportFragmentManager.addOnBackStackChangedListener {
            if (listOpened) mainButton.rotation = 180F
            else mainButton.rotation = 0F
        }

        // When the user wants to start running
        startButton.setOnClickListener() {
            if (isRecordingNewRoute) DrawingManagement.setDrawing(this, true) // Cursor leaves trail
            if (deleteButton.visibility == View.VISIBLE) fadeOut(deleteButton)
            fadeOut(startButton)
            fadeOut(mainButton)
            transitionToRunning()
            mMarkerManager.plotMarkerOnCurrentLocation()
        }

        // When the user wants to finish running
        finishButton.setOnClickListener() {
            DrawingManagement.setDrawing(this, false) // Cursor doesn't leaves trail
            fadeIn(mainButton)
            fadeOut(chronometer)
            fadeOut(finishButton)

            recordedTime =  SystemClock.elapsedRealtime()-chronometer.base

            // Saves the route if it only has more than 2 points
            if (mMarkerManager.getPoints().size > 2 && isRecordingNewRoute) openCompletedRunFragment()

            // Saves time if it is a better time
            if (!isRecordingNewRoute)
                if (PointsFile.saveBestTime(this, currentRouteFile, recordedTime)) {
                    showNewRecord()
                }

            isRecordingNewRoute = false
        }

        deleteButton.setOnClickListener() {
            fadeOut(deleteButton)
            fadeOut(startButton)
            mMap.clear()
            mMarkerManager.drawMarker()
            PointsFile.deleteFile(this, currentRouteFile)
        }

        // Main Button
        mainButton.setOnClickListener() {
            if (deleteButton.visibility == View.VISIBLE) fadeOut(deleteButton)
            isRecordingNewRoute = false
            mMap.clear()
            mMarkerManager.drawMarker()
            openRouteListFragment()
        }

        newBestTimeValue.setOnClickListener() {
            fadeOut(newBestTimeValue)
            fadeOut(newBestTimePrompt)
        }
        newBestTimePrompt.setOnClickListener() {
            fadeOut(newBestTimeValue)
            fadeOut(newBestTimePrompt)
        }
    }

    private fun setViews() {
        // Sets the views used in the main app
        mainButton = findViewById(R.id.mainButton)
        startButton = findViewById(R.id.startButton)
        finishButton = findViewById(R.id.finishButton)
        deleteButton = findViewById(R.id.deleteButton)
        chronometer = findViewById(R.id.chronometer)
        newBestTimePrompt = findViewById(R.id.bestTimeText)
        newBestTimeValue = findViewById(R.id.bestTimeNum)
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.styled_map))

        // Sets up the marker on the map
        DrawingManagement.setDrawing(this, false)
        mMarkerManager = MarkerManager(this, mMap)
        mMarkerManager.setMarker(ConversionsLocation.getCords(startLocation))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
            ConversionsLocation.getCords(startLocation),
            ConstantsZoom.MAIN_ZOOM
        ))

        // Keeps track of the position on the previous update
        var oldPosition = startLocation

        // Updates the position regularly
        updateHandler.postDelayed(object: Runnable {
            override fun run() {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null)
                        oldPosition = mMarkerManager.interpolateMarker(
                            oldPosition, location,
                            DrawingManagement.getDrawing(this@MapsActivity), isCameraMoving)
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
    Opens the fragment of the list of routes
     */
    private fun openRouteListFragment() {
        // Opens the list of routes
        if (supportFragmentManager.backStackEntryCount == 0) {

            // Passes the points into the fragment
            val listRouteFragment = ListRouteFragment()

            if (startButton.visibility == View.VISIBLE) fadeOut(startButton)
            listOpened = true

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
            listOpened = false
            supportFragmentManager.popBackStack()
        }
    }

    /**
    Opens the fragment to set the name of the just finished run
    as well as details of the distance and time ran
     */
    private fun openCompletedRunFragment() {
        // Passes the points into the fragment
        val completedRunFragment = CompletedRunFragment()
        val bundle = Bundle()
        bundle.putSerializable("points", mMarkerManager.getPoints())
        bundle.putSerializable("time", recordedTime)
        completedRunFragment.arguments = bundle

        // Animation for the list opening
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.enter_from_bottom,
                R.anim.exit_from_bottom,
                R.anim.enter_from_bottom,
                R.anim.exit_from_bottom
            )
            replace(R.id.completedRunFragment, completedRunFragment)
            addToBackStack("open")
            commit()
        }
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

    /**
     Provides the necessary transition to start running
     */
    private fun transitionToRunning() {

        // Gets the views from the layout
        val startBlock = findViewById<View>(R.id.startBlock)
        val startBlockNum = findViewById<TextView>(R.id.startBlockNums)

        // Array of colors and fades in transition pieces
        val colors: IntArray = intArrayOf(R.color.red, R.color.yellow, R.color.green)
        fadeIn(startBlock)
        fadeIn(startBlockNum)

        // Loop runs 4 times
        val handler = Handler()
        var i = 0
        handler.postDelayed(object: Runnable {
            override fun run() {
                // Changes the color every cycle
                if (i < 3) {
                    startBlock.setBackgroundColor(ContextCompat.getColor(this@MapsActivity, colors[i]))
                    startBlockNum.text = (3-i).toString()
                }

                // Looping mechanisms
                i++
                if (i != 4) handler.postDelayed(this, 1000)

                // Fades transition away and starts stop watch
                else {
                    fadeOut(startBlock)
                    fadeOut(startBlockNum)
                    fadeIn(chronometer)
                    fadeIn(finishButton)

                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.start()
                }
            }
        }, 0)
    }

    private fun showNewRecord() {

        // Setting format for best time
        val pattern = "H:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.CANADA)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val bestTime = simpleDateFormat.format(recordedTime)
        newBestTimeValue.text = bestTime

        // Fades in
        fadeIn(newBestTimeValue)
        fadeIn(newBestTimePrompt)
    }

}

