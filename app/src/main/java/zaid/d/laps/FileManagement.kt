package zaid.d.laps

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.*

object PointsFile {

    /**
    Saves the given points into paths.json
    Input: Array of LatLng objects to the user's position
    Output: none
     */
    fun savePoints(context : Context, points : Array<LatLng>) {

        // Creates a file if there is not one
        val outputStream = context.openFileOutput("paths.json", Context.MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(outputStream)

        // Writes the points in format "Lat,Long"
        for (point in points) {
            val cords = (point.longitude.toString() + "," + point.longitude.toString() + "\n")
            Log.d("Save:", cords)
            outputStreamWriter.write(cords)
        }

        outputStreamWriter.close()
    }

    /**
    Gets the saved points from paths.json
    Input: context: Context of the current activity
    Output: An array of LatLng objects of the saved points
     */
    fun readPoints(context : Context): Array<LatLng> {

        // Opens the file stream
        val inputStream = context.openFileInput("paths.json")
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        // Tool to write the cords into
        val points = mutableListOf<LatLng>()
        var reading = true

        // Reading until EOF
        while (reading) {
            when (val cords = bufferedReader.readLine()) {
                null -> reading = false
                else -> {
                    points.add(tokenizePoint(cords))
                    Log.d("Load:", cords)
                }
            }
        }
        return points.toTypedArray()
    }

    /**
    Converts given string to LatLng object
    Input: cords: String of coordinates in form of "lat,long"
    Output: LatLng object made from the given string
    */
    private fun tokenizePoint(cords: String): LatLng {
        var lat = ""
        var long = ""
        var passedComma = false

        for (c in cords) {
            if (c == ',') passedComma = true
            else if (passedComma) lat += c
            else long += c
        }
        return LatLng(lat.toDouble(), long.toDouble())
    }
}