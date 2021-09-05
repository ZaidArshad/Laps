package zaid.d.laps

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.*

object PointsFile {

    /**
    Saves the given points into paths.txt
    Input: context: Context of the activity
           routeName: Name of the route
           points: Array of the cords
    Output: none
     */
    fun savePoints(context: Context, routeName: String, bestTime: Long, points : Array<LatLng>) {

        // Gets a unique file ID
        val fileID = getNewFileID(context)
        val fileName = fileID.toString() + "path.txt"
        addFile(context, fileName)

        // Setting up to write the file
        val outputStream = context.openFileOutput(fileName, MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(outputStream)

        // Writes file id and routeName
        outputStreamWriter.write("$fileID,$routeName,$bestTime\n")

        // Writes the points in format "Lat,Long"
        for (point in points) {
            val cords = (point.latitude.toString() + "," + point.longitude.toString() + "\n")
            outputStreamWriter.write(cords)
        }

        outputStreamWriter.close()

    }

    /**
    Gets the saved points from paths.txt
    Input: context: Context of the current activity
           fileName: Name of the file to be read
    Output: An array of LatLng objects of the saved points
     */
    fun readPoints(context: Context, fileName: String): Route {
        Log.d("Reading", fileName)

        // Opens the file stream
        val inputStream = context.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        // Tool to write the cords into
        val points = mutableListOf<LatLng>()
        var reading = true

        val firstLine = tokenizeFirstLine(bufferedReader.readLine())
        val routeName = firstLine[1]
        val time = firstLine[2].toLong()

        // Reading until EOF
        while (reading) {
            when (val cords = bufferedReader.readLine()) {
                null -> reading = false
                else -> {
                    points.add(tokenizePoint(cords))
                }
            }
        }

        return Route(fileName, routeName, time, points.size, points.toTypedArray())
    }


    /**
    Saves a new time if it is better than the previous run time
    Input: context: Context of the current activity
           fileName: Name of the file to be read
           currentTime: The time of the current run
    Output: Boolean of whether the file gets overwrote
     */
    fun saveBestTime(context: Context, fileName: String, currentTime: Long): Boolean {

        // Opens the file stream
        val inputStream = context.openFileInput(fileName)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        // First line: {num, name, time}
        val firstLine = tokenizeFirstLine(bufferedReader.readLine())
        val routeID = firstLine[0]
        val routeName = firstLine[1]
        val previousTime = firstLine[2].toLong()

        // Overwrites the file with the new time if it is better
        if (currentTime < previousTime) {

            // Starts writing to file
            val outputStream = context.openFileOutput(fileName, MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(outputStream)

            // Writes file id and routeName
            outputStreamWriter.write("$routeID,$routeName,$currentTime\n")

            // Tool to write the cords into
            val points = mutableListOf<LatLng>()
            var reading = true

            // Reading the points
            while (reading) {
                when (val cords = bufferedReader.readLine()) {
                    null -> reading = false
                    else -> {
                        points.add(tokenizePoint(cords))
                    }
                }
            }

            // Writes the points
            for (point in points) {
                val cords = (point.latitude.toString() + "," + point.longitude.toString() + "\n")
                outputStreamWriter.write(cords)
            }

            outputStreamWriter.close()
            return true
        }
        return false

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
    Extracts the numbers in the beginning of the given string
    Input: fileName: The name of the file to get the ID from
    Output: The beginning few digits of the file name
     */
    private fun tokenizeFileID(fileName: String): String {
        var fileID = ""

        // Gets the numbers in the beginning of the file name
        for (c in fileName) if (c.isDigit()) fileID += c

        return fileID
    }

    /**
    Coverts the first line of the given string to an array of {num, name, time} of route
    Input: line: String of the first line of the file
    Output: Array of num and name of file
     */
    private fun tokenizeFirstLine(line: String): Array<String> {
        var num = ""
        var name = ""
        var time = ""
        var commaLevel = 0

        for (c in line) {
            if (c == ',') commaLevel++
            else {
                // Adds the comma-separated values to their vars
                when (commaLevel) {
                    0 -> num += c
                    1 -> name += c
                    2 -> time += c
                }
            }
        }
        return arrayOf(num, name, time)
    }

    /**
    Adds a txt file to the file name set in shared prefs
    Input: context: Context of the activity
           fileName: Name of the .txt file
     */
    private fun addFile(context: Context, fileName: String) {
        Log.d("adding", fileName)
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPref.edit()
        val fileNamesSet = getFileNames(context)
        val set = fileNamesSet.toMutableSet()

        // Creates a new set if it's empty
        Log.d("added:", set.add(fileName).toString())

        // Puts in the new set
        editor.putStringSet(Strings.FILENAME_SET, set)
        editor.apply()
    }

    fun deleteFile(context: Context, fileName: String) {
        Log.d("deleting", fileName)
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPref.edit()
        val fileNamesSet = sharedPref.getStringSet(Strings.FILENAME_SET, null)
        val set = fileNamesSet?.toMutableSet()

        // Deletes the file in the set
        if (fileNamesSet != null) {
            val file = File(context.filesDir,fileName)
            Log.d("deleted txt:", file.delete().toString())
            Log.d("deleted fileName:", set?.remove(fileName).toString())
            editor.putStringSet(Strings.FILENAME_SET, set)
            editor.apply()
        }

    }

    /**
    Gets the file name set from shared prefs
    Input: context: Context of the activity
    Output: Set of the filenames
     */
    fun getFileNames(context: Context): Array<String> {
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val fileNames = sharedPref.getStringSet(Strings.FILENAME_SET, null)
        if (fileNames != null && fileNames.size != 0) return fileNames.toTypedArray()
        else return arrayOf("nothing")
    }

    /**
    Gets the file ID for the next file
    Input: context: Context of the activity
    Output: Id of the next file
     */
    private fun getNewFileID(context: Context): Int {

        // Gets the id from shared prefs
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val id = sharedPref.getInt(Strings.FILE_NAME_ID, 0)

        // Returns new id and ready new one
        val editor = sharedPref.edit()
        editor.putInt(Strings.FILE_NAME_ID, id+1)
        editor.apply()
        return id
    }
}

object DrawingManagement {
    /**
    Sets the global variable to draw on the map
     Input:
        context: Context of the application
        status: Whether or not marker should draw
     */
    fun setDrawing(context: Context, status: Boolean) {
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(Strings.IS_DRAWING, status)
        editor.apply()
    }

    /**
    Sets the global variable to draw on the map
    Input: context: Context of the application
    Output : The status of whether or not the marker is drawing
     */
    fun getDrawing(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(Strings.SHARED_PREFS, MODE_PRIVATE)
        return sharedPref.getBoolean(Strings.IS_DRAWING, false)
    }
}