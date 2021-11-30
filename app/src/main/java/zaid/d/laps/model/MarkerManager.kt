package zaid.d.laps.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Handler
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import zaid.d.laps.R
import zaid.d.laps.objects.*

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
    private var points = mutableListOf<LatLng>()

    /**
    Smoothly adjusts the marker to a new location
    Input: oldPosition: The position prior to the position update
           nextPosition: The position after the position update
           draw: Whether the trail is to be drawn
           adjustCam: Whether the camera should be adjusted on every movement
    Output: The location of the where the marker has moved to
     */
    fun interpolateMarker(oldPosition: Location,nextPosition: Location, draw: Boolean, adjustCam: Boolean): Location {

        // Starting position
        var lat = oldPosition.latitude
        var long = oldPosition.longitude
        val lineDetails = PolylineOptions()
        val polyline = mMap.addPolyline(lineDetails)
        polyline.width = ConstantsLine.LINE_WIDTH

        // Rotation
        val dRotation = equivalentAngle(nextPosition.bearing-oldPosition.bearing) / ConstantsTime.FRAME_CAP

        // Distance to increment every frame
        val dLatitude = (nextPosition.latitude - lat) / ConstantsTime.FRAME_CAP
        val dLongitude = (nextPosition.longitude- long) / ConstantsTime.FRAME_CAP

        // Keeps track of looping
        var frame = 0
        val incrementTime = ConstantsTime.DELAY_TIME / ConstantsTime.FRAME_CAP
        val handler = Handler()

        // If the position has changed start animating
        if (ConversionsLocation.isGapReached(oldPosition, nextPosition)) {
            handler.post(object : Runnable {
                override fun run() {
                    lat += dLatitude
                    long += dLongitude
                    personMarker.position = LatLng(lat, long)
                    personMarker.rotation += dRotation

                    if (draw) {
                        points.add(LatLng(lat, long))
                        polyline.points = points
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

            if (adjustCam) {
                val cam = CameraPosition.Builder()
                    .bearing(nextPosition.bearing)
                    .zoom(ConstantsZoom.MAIN_ZOOM)
                    .target(ConversionsLocation.getCords(nextPosition))
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam.build()))
            }


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
            mContext, R.drawable.ic_usermarkerslimborderless
        )!!.toBitmap()
        myPerson.icon(BitmapDescriptorFactory.fromBitmap(myPersonIcon))

        // Puts the created marker on map
        personMarker = mMap.addMarker(myPerson)!!
        plotMarkerOnCurrentLocation()
    }

    fun drawMarker() {
        val myPersonIcon = AppCompatResources.getDrawable(
            mContext, R.drawable.ic_usermarkerslimborderless
        )!!.toBitmap()
        myPerson.icon(BitmapDescriptorFactory.fromBitmap(myPersonIcon))

        // Puts the created marker on map
        personMarker = mMap.addMarker(myPerson)!!
    }

    /**
    Updates the marker's location on the map when called
     */
    @SuppressLint("MissingPermission")
    fun plotMarkerOnCurrentLocation() {

        // Gets the current location
        PermissionsLocation.locationPermissionCheck(mContext)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        fusedLocationClient.lastLocation.addOnSuccessListener{ location: Location? ->

            // Shifts the camera to the location and plots the point
            personMarker.position = ConversionsLocation.getCords(location!!)
            personMarker.rotation = location.bearing
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ConversionsLocation.getCords(location), ConstantsZoom.MAIN_ZOOM))
        }

    }

    /**
    Gets an equivalent from the given angle
    Input: angle: Angle to be compared to
    Output: Angle with the same heading as given angle but absolutely smaller
     */
    private fun equivalentAngle(angle: Float): Float {
        if (angle > 180) return (angle - 360)
        else if (angle < -180) return (angle + 360)
        return angle
    }

    fun getPoints(): Array<LatLng> {
        return (points.toTypedArray())
    }


}