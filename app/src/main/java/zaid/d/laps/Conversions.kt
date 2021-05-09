package zaid.d.laps

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object ConversionsLocation {
    /**
    Parses data from location object to a LatLng object
    Input: location: The location to be parsed
    Output: Latitude and longitude of given location
     */
    fun getCords(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
}