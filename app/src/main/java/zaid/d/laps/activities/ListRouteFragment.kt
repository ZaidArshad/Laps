package zaid.d.laps.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_maps.*
import zaid.d.laps.*
import zaid.d.laps.model.Route
import zaid.d.laps.model.RouteListAdapter
import zaid.d.laps.objects.ConversionsSort
import zaid.d.laps.objects.PointsFile

class ListRouteFragment : Fragment(R.layout.fragment_list_route) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = activity?.findViewById<ListView>(R.id.routeListView)
        val context = activity!!.applicationContext

        // Gets the names of the files
        val items = PointsFile.getFileNames(context)
        ConversionsSort.sortFiles(items)
        val routeList = ArrayList<Route>()

        // If there are files to read, read them
        for (fileName in items) {
            if (fileName == "nothing") {
                break
            }
            Log.d("loaded", fileName)
            routeList.add(PointsFile.readPoints(context, fileName))
        }

        // Creates the footer button for the list
        val addButton = Button(context)
        addButton.setText(R.string.record_new_route)
        addButton.typeface = Typeface.DEFAULT_BOLD
        addButton.setTextColor(Color.parseColor("#FFFF78"))
        addButton.setBackgroundColor(Color.parseColor("#ED1B4D"))
        addButton.textSize = 25F
        addButton.height = 175
        listView?.addFooterView(addButton)

        // Creates the list of the items
        val routeListAdapter = RouteListAdapter(context, R.layout.adapter_routes, routeList, (activity as MapsActivity))
        listView?.adapter = routeListAdapter
        listView?.isClickable = true

        // Create record new route button click
        addButton.setOnClickListener {

            // Starts drawing path and recording
            (activity as MapsActivity).fadeIn(activity!!.startButton)
            (activity as MapsActivity).listOpened = false
            (activity as MapsActivity).isRecordingNewRoute = true

            activity?.supportFragmentManager?.popBackStack()
        }

    }
}