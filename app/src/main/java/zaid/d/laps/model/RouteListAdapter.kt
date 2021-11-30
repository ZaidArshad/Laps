package zaid.d.laps.model

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.PolylineOptions
import zaid.d.laps.R
import zaid.d.laps.activities.MapsActivity
import zaid.d.laps.objects.ConstantsLine
import zaid.d.laps.objects.ConversionsLocation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RouteListAdapter(context: Context, resource: Int, objects: ArrayList<Route>, activity: MapsActivity) :
    ArrayAdapter<Route>(
        context,
        resource,
        objects
    ) {
    private val TAG = "RouteListAdapter"
    private val mContext = context
    private val mResource = resource
    private val mActivity = activity

    @SuppressLint("ViewHolder", "ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Sets the attributes for each route object
        val name = getItem(position)!!.getRouteName()
        val bestTime = getItem(position)!!.getBestTime()
        val length = getItem(position)!!.getDistance()

        // Setting the length to the right units
        var formattedLength = ""
        if (length < 1000)
            formattedLength = length.toString() + "M"
        else
            formattedLength = String.format("%.1fKM",length.toDouble()/1000)


        val pattern = "H:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.CANADA)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        var date = simpleDateFormat.format(bestTime)
        date = "PR: $date"

        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(mResource, parent, false)

        // Sets the text views in the layout
        val nameView = view.findViewById<TextView>(R.id.routeNameEdit)
        val distanceView = view.findViewById<TextView>(R.id.routeLengthText)
        val timeView = view.findViewById<TextView>(R.id.timeRecordedText)
        distanceView.text = formattedLength
        nameView.text = name
        timeView.text = date

        view.setOnClickListener() {

            // Clear map and draw current route
            mActivity.mMap.clear()
            mActivity.mMarkerManager.drawMarker()
            val lineDetails = PolylineOptions()
            val points = getItem(position)!!.getPoints()
            val polyline = mActivity.mMap.addPolyline(lineDetails)
            polyline.width = ConstantsLine.LINE_WIDTH
            polyline.isGeodesic = false
            polyline.points = points.toMutableList()

            // Camera to current route
            if (points.size > 2) {
                val bounds = ConversionsLocation.getBounds(points)
                mActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }

            mActivity.fadeIn(mActivity.deleteButton)
            mActivity.fadeIn(mActivity.startButton)
            mActivity.listOpened = false
            mActivity.isCameraMoving = false

            mActivity.currentRouteFile = getItem(position)!!.getFileName()

            mActivity.supportFragmentManager.popBackStack()
        }

        return view
    }
}