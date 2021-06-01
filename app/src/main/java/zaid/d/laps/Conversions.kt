package zaid.d.laps

import android.location.Location
import com.google.android.gms.maps.model.LatLng
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

    fun optimizeCords(points: Array<LatLng>): Array<LatLng> {
        val optimized = mutableListOf<LatLng>()
        var lastPoint = LatLng(0.0,0.0)
        for (point in points) {
            if (isGapReached(lastPoint, point)) optimized.add(point)
            lastPoint = point
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