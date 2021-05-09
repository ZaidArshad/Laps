package zaid.d.laps

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
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
            Log.d("$lat,$long to", "${nextPosition.latitude}, ${nextPosition.longitude}")
            personMarker.position = nextPosition
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nextPosition, 18f))
        }
    }

    /**
    Creates the marker on the map to the current location of the user
    Input: None
    Output: None
     */
    fun setMarker() {
        // Getting position
        val trackingClient = TrackingClient(mContext)
        val myLatLong = trackingClient.getLatLong()

        // Marker position setting
        myPerson = MarkerOptions().position(myLatLong)
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
    Input: None
    Output: None
     */
    private fun plotMarkerOnCurrentLocation() {
        val trackingClient = TrackingClient(mContext)
        val myLatLong = trackingClient.getLatLong()

        // Shifts the camera to the location and plots the point
        personMarker.position = myLatLong
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLong, 18f))
    }

    fun getMarkerPosition(): LatLng {
        return personMarker.position
    }
    fun getAdjustedMarkerPosition(): LatLng {
        val lat = personMarker.position.latitude + 0.000005
        val long = personMarker.position.longitude + 0.000005
        return LatLng(lat, long)
    }


}