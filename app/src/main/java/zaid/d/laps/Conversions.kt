package zaid.d.laps

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlin.math.sqrt

object ConversionsLocation {
    /**
    Parses data from location object to a LatLng object
    Input: location: The location to be parsed
    Output: Latitude and longitude of given location
     */
    fun getCords(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    /**
    Gets the 2 most extreme points of the given array
    Input: points: LatLng array made up of a route
    Output: Bounds of the given array
     */
    fun getBounds(points: Array<LatLng>): LatLngBounds {

        // Setting up variables
        var northEast = points.last()
        var southWest = points.first()

        // Comparing with the extreme points
        for (point in points) {
            if (getLatLongSum(point) > getLatLongSum(northEast)) northEast = point
            else if (getLatLongSum(point) < getLatLongSum(southWest)) southWest = point
        }

        // Sets the bounds
        return LatLngBounds(southWest, northEast)
    }

    private fun getLatLongSum(point: LatLng): Double {
        return point.latitude + point.longitude
    }

    /**
     Cuts down the amount of points in the inputted array
     Input: points: The array of LatLng points to be optimized
     Output: The optimized array
     */
    fun optimizeCords(points: Array<LatLng>): Array<LatLng> {
        val optimized = mutableListOf<LatLng>()
        var lastPoint = LatLng(0.0,0.0)
        for (point in points) {
            if (isGapReached(lastPoint, point))  {
                optimized.add(point)
                lastPoint = point
            }
        }
        return optimized.toTypedArray()
    }

    /**
    Checks if the distance between 2 locations is greater than the min gap
    Input: locationA: Location or LatLng object to be compared with locationB
    locationB: Location or LatLng object to be compared with locationA
    Output: Boolean state if whether the threshold radius is met
     */
    fun isGapReached(locationA: Location, locationB: Location): Boolean {
        val dLat = kotlin.math.abs(locationA.latitude - locationB.latitude)
        val dLong = kotlin.math.abs(locationA.longitude - locationB.longitude)
        val radius = sqrt((dLat*dLat) + (dLong*dLong))
        return (radius >= ConstantsDistance.MIN_GAP)
    }
    fun isGapReached(locationA: LatLng, locationB: LatLng): Boolean {
        val dLat = kotlin.math.abs(locationA.latitude - locationB.latitude)
        val dLong = kotlin.math.abs(locationA.longitude - locationB.longitude)
        val radius = sqrt((dLat*dLat) + (dLong*dLong))
        return (radius >= ConstantsDistance.MIN_GAP)
    }
}

object ConversionsSort {

    /** Bubble sort for given json files
    Input: set: Set of the filenames
     */
    fun sortFiles(set: Array<String>): Array<String> {
        for (i in set.indices)
            for (j in 0 until set.size-i-1)
                if (set[j][0] > set[j+1][0])
                    set[j] = set[j+1].also {set[j+1] = set[j] }
        return set
    }
}