package zaid.d.laps

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed

class SplashActivity: AppCompatActivity() {

    private lateinit var mLoadingBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        // Loading bar on splash screen
        mLoadingBar = findViewById<ProgressBar>(R.id.loadingBar)
        val trackingClient = TrackingClient(this)
        trackingClient.getCurrentLocation()
        runApp(trackingClient)

    }

    private fun runApp(trackingClient: TrackingClient) {

        Handler().postDelayed({
            if (!trackingClient.isNetworkConnected()) {
                Log.d("Splash", "No network connection")
                runApp(trackingClient)
            }
            else {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }
}