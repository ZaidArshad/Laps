package zaid.d.laps

import android.graphics.Color
import android.net.MailTo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.system.exitProcess

class ListRouteFragment : Fragment(R.layout.fragment_list_route) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        val listView = activity?.findViewById<ListView>(R.id.routeListView)
        val context = activity!!.applicationContext

        // Gets the names of the files
        val items = PointsFile.getFileNames(context)
        ConversionsSort.sortFiles(items)
        val routeList = ArrayList<Route>()

        // If there are files to read, read them
        if (items[0] == "1path.json")
            for (i in 1..items.size) {
                routeList.add(PointsFile.readPoints(context, i))
        }

        // Creates the footer button for the list
        val addButton = Button(context)
        addButton.setText(R.string.record_new_route)
        addButton.setTextColor(Color.parseColor("#FFFF78"))
        addButton.setBackgroundColor(Color.parseColor("#ED1B4D"))
        addButton.height = 150
        listView?.addFooterView(addButton)

        // Creates the list of the items
        val routeListAdapter = RouteListAdapter(context, R.layout.adapter_routes, routeList)
        listView?.adapter = routeListAdapter
        listView?.isClickable = true

        // Create record new route button click
        addButton.setOnClickListener {
            // val points = arguments?.getSerializable("points") as Array<LatLng>
            // PointsFile.savePoints(context, "testing", System.currentTimeMillis(),ConversionsLocation.optimizeCords(points))

            // Starts drawing path and recording
            (activity as MapsActivity).fadeIn(activity!!.startButton)

            activity?.supportFragmentManager?.popBackStack()
        }

    }
}