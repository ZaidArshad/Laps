package zaid.d.laps

import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

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
        personMarker = mMap.addMarker(myPerson)
    }

    /**
    Updates the marker's location on the map when called
    Input: None
    Output: None
     */
    fun plotMarkerOnCurrentLocation() {
        val trackingClient = TrackingClient(mContext)
        val myLatLong = trackingClient.getLatLong()

        // Shifts the camera to the location and plots the point
        personMarker.position = myLatLong
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLong, 20f))
    }


}