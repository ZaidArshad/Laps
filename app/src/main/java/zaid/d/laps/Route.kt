package zaid.d.laps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

/**
 Class that keeps track of already recorded routes, READ ONLY
 Inputs: routeNum: Value of which route this is relevant to the first one
         routeName: User given name of the path
         distance: Length of the route in meters
         points: Points of the users location
 */
class Route(routeNum : Int, routeName: String, distance: Int,points : Array<LatLng>) {
    private val mRouteNum =  routeNum
    private val mPoints = points
    private val mDistance = distance
    private val mRouteName = routeName

    /** Returns the points attribute */
    fun getPoints(): Array<LatLng> { return mPoints }

    /** Returns the route number attribute */
    fun getRouteNum(): Int { return mRouteNum }

    /** Returns the route name attribute */
    fun getRouteName(): String { return mRouteName }

    /** Returns the length in meters of the path */
    fun getDistance(): Int { return mDistance }

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