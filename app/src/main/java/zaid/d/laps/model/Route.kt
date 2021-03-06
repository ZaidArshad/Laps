package zaid.d.laps.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

/**
 Class that keeps track of already recorded routes, READ ONLY
 Inputs: routeNum: Value of which route this is relevant to the first one
         routeName: User given name of the path
         distance: Length of the route in meters
         points: Points of the users location
 */
class Route(fileName: String, routeName: String, time: Long, distance: Int, points: Array<LatLng>) {
    private val mFileName =  fileName
    private val mPoints = points
    private val mDistance = distance
    private val mRouteName = routeName
    private val mBestTime = time

    /** Returns the points attribute */
    fun getPoints(): Array<LatLng> { return mPoints }

    /** Returns the route number attribute */
    fun getFileName(): String { return mFileName }

    /** Returns the route name attribute */
    fun getRouteName(): String { return mRouteName }

    /** Returns the length in meters of the path */
    fun getDistance(): Int { return mDistance }

    /** Returns the best time on the route */
    fun getBestTime(): Long { return mBestTime }

    /**
    Creates a PolyLineOptions object from the points to
    be added to the map
     */
    fun getPolyLineOptions(): PolylineOptions {
        val polylineOption = PolylineOptions()

        // Increments through the point and add them individually
        for (point in mPoints) {
            polylineOption.add(point)
        }
        return polylineOption
    }
}