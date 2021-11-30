package zaid.d.laps.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import zaid.d.laps.objects.ConstantsTime
import zaid.d.laps.objects.PermissionsLocation
import zaid.d.laps.R

/**
Activity when the app is opened
Opens the main activity once the phone is connected to the internet
 */
class SplashActivity: AppCompatActivity() {

    private lateinit var mLoadingBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        // Loading bar on splash screen
        mLoadingBar = findViewById<ProgressBar>(R.id.loadingBar)

        // Runs the main activity once the location is established
        runApp()
    }

    /**
    Activity when the app is opened
    Opens the main activity once the phone is connected to the internet
     */
    @SuppressLint("MissingPermission")
    private fun runApp() {
        PermissionsLocation.locationPermissionCheck(this)

        // Keeping track of login
        val handler = Handler()
        val intent = Intent(this, MapsActivity::class.java)
        var isReady = false

        // Checks if a location has been found
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->
            isReady = true
            intent.putExtra("startLocation",location)
            Log.d("Splash", "Location Found!")
        }

        // Wait 2 seconds for the location, if not found recursive call to check again
        handler.postDelayed({
            if (isReady) {
                startActivity(intent)
                finish()
            } else {
                Log.d("Splash", "No location found")
                runApp()
            }
        }, ConstantsTime.DELAY_TIME)


    }
}