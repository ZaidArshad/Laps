package zaid.d.laps

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class ListRouteFragment : Fragment(R.layout.fragment_list_route) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        val listView = activity?.findViewById<ListView>(R.id.routeListView)
        val context = activity!!.applicationContext

        var items = PointsFile.getFileNames(context)
        items = ConversionsSort.sortFiles(items)

        val routeList = ArrayList<Route>()

        for (i in 1..items.size) {
            routeList.add(PointsFile.readPoints(context, i))
        }

        val routeListAdapter = RouteListAdapter(context, R.layout.adapter_routes, routeList)
        listView?.adapter = routeListAdapter
    }
}