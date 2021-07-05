package zaid.d.laps

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RouteListAdapter(context: Context, resource: Int, objects: ArrayList<Route>) :
    ArrayAdapter<Route>(
        context,
        resource,
        objects
    ) {
    private val TAG = "RouteListAdapter"
    private val mContext = context
    private val mResource = resource

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Sets the attributes for each route object
        val name = getItem(position)!!.getRouteName()
        val bestTime = getItem(position)!!.getBestTime()
        val length = getItem(position)!!.getDistance()

        val formattedLength =  BigDecimal(length/1000).setScale(2, RoundingMode.HALF_EVEN).toString() + "KM"

        val pattern = "h:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.CANADA)
        var date = simpleDateFormat.format(bestTime)
        date = "PR: $date"

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(mResource, parent, false)

        // Sets the text views in the layout
        val nameView = view.findViewById<TextView>(R.id.routeName)
        val distanceView = view.findViewById<TextView>(R.id.routeLength)
        val timeView = view.findViewById<TextView>(R.id.bestTime)
        distanceView.text = formattedLength
        nameView.text = name
        timeView.text = date

        view.setOnClickListener() {
            Log.d("clicked", date)
        }

        return view
    }
}