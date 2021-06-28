package zaid.d.laps

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.*

object PointsFile {

    /**
    Saves the given points into paths.json
    Input: context: Context of the activity
           routeName: Name of the route
           points: Array of the cords
    Output: none
     */
    fun savePoints(context : Context, routeName: String, points : Array<LatLng>) {

        val fileName: String
        val numOfFiles: Int

        // Creates a file if there is not one
        if (getFileNames(context)[0] == "nothing") {
            fileName = "1path.json"
            addFile(context, "1path.json")
            numOfFiles = 1
        }
        // If there is already a file
        else {
            numOfFiles = getFileNames(context).size + 1
            fileName = numOfFiles.toString() + "path.json"
            addFile(context, fileName)
        }

        // Setting up to write the file
        val outputStream = context.openFileOutput(fileName, MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(outputStream)

        // Writes file num and routeName
        outputStreamWriter.write("$numOfFiles,$routeName\n")

        // Writes the points in format "Lat,Long"
        for (point in points) {
            val cords = (point.longitude.toString() + "," + point.longitude.toString() + "\n")
            outputStreamWriter.write(cords)
        }

        outputStreamWriter.close()
    }

    /**
    Gets the saved points from paths.json
    Input: context: Context of the current activity
    Output: An array of LatLng objects of the saved points
     */
    fun readPoints(context : Context, routeNum : Int): Route {

        // Opens the file stream
        val fileName = routeNum.toString() + "path.json"
        val inputStream = context.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        // Tool to write the cords into
        val points = mutableListOf<LatLng>()
        var reading = true

        val routeName = tokenizeNumName(bufferedReader.readLine())[1]

        // Reading until EOF
        while (reading) {
            when (val cords = bufferedReader.readLine()) {
                null -> reading = false
                else -> {
                    points.add(tokenizePoint(cords))
                }
            }
        }
        return Route(routeNum, routeName, points.size,points.toTypedArray())
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
            else if (passedComma) long += c
            else lat += c
        }
        return LatLng(lat.toDouble(), long.toDouble())
    }

    /**
    Coverts the first line of the given string to an array of {num, name} of route
    Input: line: String of the first line of the file
    Output: Array of num and name of file
     */
    private fun tokenizeNumName(line: String): Array<String> {
        var num = ""
        var name = ""
        var passedComma = false

        for (c in line) {
            if (c == ',') passedComma = true
            else if (passedComma) name += c
            else num += c
        }
        return arrayOf(num, name)
    }

    /**
    Adds a json file to the file name set in shared prefs
    Input: context: Context of the activity
           fileName: Name of the .json file
     */
    private fun addFile(context: Context, fileName: String) {
        Log.d("fileName", fileName)
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPref.edit()
        val fileNamesSet = sharedPref.getStringSet(Strings.FILENAME_SET, null)
        var set = mutableSetOf<String>()

        // Creates a new set if it's empty
        if (fileNamesSet == null) set.add(fileName)
        else if (fileName == "1path.json") set.add(fileName)

        // Add filename to preexisting set if not already in
        else if (fileName !in fileNamesSet) {
            fileNamesSet.add(fileName)
            set = fileNamesSet
        }

        // Puts in the new set
        editor.clear()
        editor.putStringSet(Strings.FILENAME_SET, set)
        editor.apply()
    }

    /**
    Gets the file name set from shared prefs
    Input: context: Context of the activity
    Output: Set of the filenames
     */
    fun getFileNames(context: Context): Array<String> {
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val fileNames = sharedPref.getStringSet(Strings.FILENAME_SET, null)
        if (fileNames != null) return fileNames.toTypedArray()
        else return arrayOf("nothing")
    }
}