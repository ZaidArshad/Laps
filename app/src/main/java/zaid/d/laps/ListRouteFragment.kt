package zaid.d.laps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
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
        val routeList = ArrayList<Route>()

        // If there are files to read, read them
        if (items[0] != "nothing")
            for (i in 1..items.size) {
                routeList.add(PointsFile.readPoints(context, i))
        }

        // Creates the footer button for the list
        val addButton = Button(context)
        addButton.setText(R.string.record_new_route)
        listView?.addFooterView(addButton)

        // Creates the list of the items
        val routeListAdapter = RouteListAdapter(context, R.layout.adapter_routes, routeList)
        listView?.adapter = routeListAdapter

        // Creates a new route
        addButton.setOnClickListener {
            val points = arguments?.getSerializable("points") as Array<LatLng>
            PointsFile.savePoints(context, "testing", System.currentTimeMillis(),ConversionsLocation.optimizeCords(points))
            activity?.supportFragmentManager?.popBackStack()
        }

    }
}