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
import kotlin.math.sqrt

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
    private var points = setOf<LatLng>()

    /**
    Smoothly adjusts the marker to a new location
    Input: oldPosition: The position prior to the position update
           nextPosition: The position after the position update
           draw: The
    Output: The location of the where the marker has moved to
     */
    fun interpolateMarker(oldPosition: Location,nextPosition: Location, draw: Boolean): Location {

        // Starting position
        var lat = oldPosition.latitude
        var long = oldPosition.longitude
        val lineDetails = PolylineOptions()
        val polyline = mMap.addPolyline(lineDetails)

        // Rotation
        val dRotation = oldPosition.bearingTo(nextPosition) / ConstantsTime.FRAME_CAP

        // Distance to increment every frame
        val dLatitude = (nextPosition.latitude - lat) / ConstantsTime.FRAME_CAP
        val dLongitude = (nextPosition.longitude- long) / ConstantsTime.FRAME_CAP

        // Keeps track of looping
        var frame = 0
        val incrementTime = ConstantsTime.DELAY_TIME / ConstantsTime.FRAME_CAP
        val handler = Handler()

        // If the position has changed start animating
        if (isGapReached(oldPosition, nextPosition)) {
            handler.post(object : Runnable {
                override fun run() {
                    lat += dLatitude
                    long += dLongitude
                    Log.d("", "lat, long = $lat, $long")
                    personMarker.position = LatLng(lat, long)
                    personMarker.rotation += dRotation

                    if (draw) {
                        points = points.plus(LatLng(lat, long))
                        polyline.points = points.toMutableList()
                    }

                    // Does 30 frames of adjusting
                    if (frame < ConstantsTime.FRAME_CAP) {
                        frame++
                        handler.postDelayed(this, incrementTime)
                    }
                }
            })

            // Sets the final location to the exact spot
            personMarker.position = ConversionsLocation.getCords(nextPosition)
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                //ConversionsLocation.getCords(nextPosition), ConstantsZoom.MAIN_ZOOM))
            val cam = CameraPosition.Builder()
                .bearing(oldPosition.bearingTo(nextPosition))
                .zoom(ConstantsZoom.MAIN_ZOOM)
                .target(ConversionsLocation.getCords(nextPosition))
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam.build()))


            return nextPosition
        }
        return oldPosition
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

    /**
     Checks if the distance between 2 locations is greater than the min gap
     Input: locationA: Location object to be compared with locationB
            locationB: Location object to be compared with locationA
     Output: Boolean state if whether the threshold radius is met
     */
    private fun isGapReached(locationA: Location, locationB: Location): Boolean {
        val dLat = kotlin.math.abs(locationA.latitude - locationB.latitude)
        val dLong = kotlin.math.abs(locationA.longitude - locationB.longitude)
        val radius = sqrt((dLat*dLat) + (dLong*dLong))
        return (radius >= ConstantsDistance.MIN_GAP)
    }


}