package zaid.d.laps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

/**
 Class that keeps track of already recorded routes, READ ONLY
 Inputs: routeNum: Value of which route this is relevant to the first one
         points: Points of the users location
 */
class Route(routeNum : Int, routeName: String, points : Array<LatLng>) {
    private val mRouteNum =  routeNum
    private val mPoints = points
    private val mRouteName = routeName

    /** Returns the points attribute */
    fun getPoints(): Array<LatLng> { return mPoints }

    /** Returns the route number attribute */
    fun getRouteNum(): Int { return mRouteNum }

    /** Returns the route name attribute */
    fun getRouteName(): String { return mRouteName }

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