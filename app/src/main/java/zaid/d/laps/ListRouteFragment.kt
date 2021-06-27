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
        val listView = activity?.findViewById<ListView>(R.id.routeListView)

        var items = PointsFile.getFileNames(activity!!.applicationContext)
        items = ConversionsSort.sortFiles(items)

        val arrayAdapter = ArrayAdapter(activity!!,android.R.layout.simple_list_item_1, items)
        listView?.adapter = arrayAdapter
    }
}