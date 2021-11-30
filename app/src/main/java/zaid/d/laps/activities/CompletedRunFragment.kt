package zaid.d.laps.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import zaid.d.laps.objects.ConstantsDistance
import zaid.d.laps.objects.ConversionsLocation
import zaid.d.laps.objects.PointsFile
import zaid.d.laps.R
import java.text.SimpleDateFormat
import java.util.*

class CompletedRunFragment : Fragment(R.layout.fragment_completed_run) {

    private lateinit var routeNameEdit: EditText
    private lateinit var routeLengthText: TextView
    private lateinit var timeRecordedText: TextView
    private lateinit var confirmButton: Button
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sets the layout objects
        routeNameEdit = view.findViewById(R.id.routeNameEdit)
        routeLengthText = view.findViewById(R.id.routeLengthText)
        timeRecordedText = view.findViewById(R.id.timeRecordedText)
        confirmButton = view.findViewById(R.id.confirmButton)
        mContext = activity!!.applicationContext

        // Greys out the button
        confirmButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey))
        confirmButton.setTextColor(ContextCompat.getColor(mContext, R.color.darkGrey))

        // Checks if the text box is filled out and lets the user press the button
        routeNameEdit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setButtonClickable(false)
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if (text!!.isNotEmpty()) {
                    setButtonClickable(true)
                }
            }

        })

        // Getting the points of the path
        var points = arguments!!.getSerializable("points") as Array<LatLng>
        points = ConversionsLocation.optimizeCords(points)
        val length = (points.size* ConstantsDistance.METERS_PER_POINT).toString() + " M"

        // Getting the time ran
        val time = arguments?.getSerializable("time") as Long
        val pattern = "H:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.CANADA)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        var timeRan = simpleDateFormat.format(time)
        timeRan = "Time: $timeRan"

        // Setting the layout objects
        routeLengthText.text = length
        timeRecordedText.text = timeRan

        // When the confirm button is clicked save the route and close fragment
        confirmButton.setOnClickListener() {
            PointsFile.savePoints(mContext, routeNameEdit.text.toString(), time, points)
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    /**
    Sets the confirm button's click status and grey it out/colors depending on that
    Input: status: Boolean of the status to set the confirm button
     */
    private fun setButtonClickable(status: Boolean) {
        confirmButton.isClickable = status

        if (status) {
            confirmButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            confirmButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
        }
        else {
            confirmButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey))
            confirmButton.setTextColor(ContextCompat.getColor(mContext, R.color.darkGrey))
        }
    }
}