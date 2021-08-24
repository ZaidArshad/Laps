package zaid.d.laps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

class CompletedRunFragment : Fragment(R.layout.fragment_completed_run) {

    private lateinit var routeNameEdit: EditText
    private lateinit var routeLengthText: TextView
    private lateinit var timeRecordedText: TextView
    private lateinit var confirmButton: Button

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

        // Greys out the button
        confirmButton.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
        confirmButton.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.darkGrey))

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

        // When the confirm button is clicked
        confirmButton.setOnClickListener() {
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
            confirmButton.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
            confirmButton.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
        }
        else {
            confirmButton.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.grey))
            confirmButton.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.darkGrey))
        }
    }
}