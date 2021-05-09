package zaid.d.laps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Handler
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

/**
Class to manage the Marker() object on the google maps fragment
Input: context: Context of the current activity
       map: The google maps fragment from activity
 */
class MarkerManager(context: Context, map: GoogleMap) {
    private var mContext = context
    private var mMap = map
    private lateinit var myPerson: MarkerOptions
    private lateinit var personMarker: Marker

    /**
    Smoothly adjusts the marker to a new location
    Input: oldPosition: The position prior to the position update
           nextPosition: The position after the position update
    Output: None
     */
    fun interpolateMarker(oldPosition: LatLng,nextPosition: LatLng) {

        // Starting position
        var lat = oldPosition.latitude
        var long = oldPosition.longitude

        // Distance to increment every frame
        val dLatitude = (nextPosition.latitude - lat) / ConstantsTime.FRAME_CAP
        val dLongitude = (nextPosition.longitude- long) / ConstantsTime.FRAME_CAP

        // Keeps track of looping
        var frame = 0
        val incrementTime = ConstantsTime.DELAY_TIME / ConstantsTime.FRAME_CAP
        val handler = Handler()

        // If the position has changed start animating
        if (nextPosition.latitude != lat && nextPosition.longitude != long) {
            handler.post(object : Runnable {
                override fun run() {
                    lat += dLatitude
                    long += dLongitude
                    Log.d("", "lat, long = $lat, $long")
                    personMarker.position = LatLng(lat, long)

                    // Does 30 frames of adjusting
                    if (frame < ConstantsTime.FRAME_CAP) {
                        frame++
                        handler.postDelayed(this, incrementTime)
                    }
                }
            })

            // Sets the final location to the exact spot
            personMarker.position = nextPosition
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nextPosition, 18f))
        }
    }

    /**
    Creates the marker on the map to the current location of the user
     */
    fun setMarker(cords: LatLng) {

        // Marker position setting
        myPerson = MarkerOptions().position(cords)
        myPerson.flat(true)
        myPerson.anchor(0.5F, 0.5F)

        // Getting the marker icon
        val myPersonIcon = AppCompatResources.getDrawable(
            mContext, R.drawable.ic_usermarkerslimborderless)!!.toBitmap()
        myPerson.icon(BitmapDescriptorFactory.fromBitmap(myPersonIcon))

        // Puts the created marker on map
        personMarker = mMap.addMarker(myPerson)!!
        plotMarkerOnCurrentLocation()
    }

    /**
    Updates the marker's location on the map when called
     */
    @SuppressLint("MissingPermission")
    private fun plotMarkerOnCurrentLocation() {

        // Gets the current location
        PermissionsLocation.locationPermissionCheck(mContext)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->

            // Shifts the camera to the location and plots the point
            personMarker.position = ConversionsLocation.getCords(location!!)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ConversionsLocation.getCords(location), ConstantsZoom.MAIN_ZOOM))
        }

    }


}