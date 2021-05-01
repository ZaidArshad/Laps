package zaid.d.laps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.model.LatLng

class TrackingClient (context: Context) {

    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var mContext = context
    var mLocationClient= mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
    Gets the current location
    Input: none
    Output: The current latitude and longitude of the user is a connection is established
    If connection is not established outputs null statement
     */
    fun getCurrentLocation(): LatLng? {


        // Gets the status of whether or not the connection is established to the network
        mLocationClient = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = mLocationClient.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = mLocationClient.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // Permission checking of location
        while (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                mContext as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                10)
        }

        // Gps connection established
        if (hasGps) {
            mLocationClient.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 0F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) locationGps = location
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProviderEnabled(provider: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProviderDisabled(provider: String?) {
                        TODO("Not yet implemented")
                    }

                })

            // Sets the
            val localGpsLocation =
                mLocationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null) locationGps = localGpsLocation
        }

        // Network connection established
        if (hasNetwork) {
            mLocationClient.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) locationNetwork = location
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProviderEnabled(provider: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onProviderDisabled(provider: String?) {
                        TODO("Not yet implemented")
                    }

                })

            val localNetworkLocation =
                mLocationClient.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (localNetworkLocation != null) locationNetwork = localNetworkLocation
        }

        // If both providers are connected use the more accurate one
        if (locationNetwork != null && locationGps != null) {
            Log.d("getLocation()", "Network and Gps Connection")
            return if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                LatLng(locationGps!!.latitude, locationGps!!.longitude)
            } else {
                LatLng(locationNetwork!!.latitude, locationNetwork!!.longitude)
            }
        }
        // If the network provider is connected use those coordinates
        else if (locationNetwork != null) {
            Log.d("getLocation()", "Network Connection")
            return LatLng(locationNetwork!!.latitude, locationNetwork!!.longitude)
        }
        // If the gps provider is connected use those coordinates
        else if (locationGps != null) {
            Log.d("getLocation()", "Gps Connection")
            return LatLng(locationGps!!.latitude, locationGps!!.longitude)
        }
        // If neither network is connected
        else {
            var connected = "No:"
            if (locationNetwork == null) connected += " Network"
            if (locationGps == null) connected += " Gps"
            Log.d("getLocation()", connected)
            return null
        }

    }

    /**
    Gets the status of the devices internet connection
    Input: none
     Output The status of the device's connection to the internet
    */
    fun isNetworkConnected(): Boolean {

        // Gets the status of the network
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        // If there is a network return true otherwise false
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d("isNetworkConnected()", "Wifi Connection")
                return true
            }
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d("isNetworkConnected()", "Cellular Connection")
                return true
            }
        }
        return false
    }
}
