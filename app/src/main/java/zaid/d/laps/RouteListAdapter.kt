package zaid.d.laps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.DragEvent.ACTION_DROP
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

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

        val formattedLength = "$length M"

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