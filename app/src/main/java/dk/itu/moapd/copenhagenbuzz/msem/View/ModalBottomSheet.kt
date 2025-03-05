package dk.itu.moapd.copenhagenbuzz.msem.View
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.util.component1
import androidx.core.util.component2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.BottomSheetContentBinding

class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomBinding: BottomSheetContentBinding
    private val event: Event = Event("", "", "", "", "")
    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventType: String
    private lateinit var dateRangeField: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.bottom_sheet_content, container, false)
        bottomBinding = BottomSheetContentBinding.inflate(inflater)
        return bottomBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
    super.onViewCreated(view, savedInstanceState)
        //bottomBinding = BottomSheetContentBinding.inflate(layoutInflater)
        bottomBinding.editTextEventName.setText(event.eventName)
        bottomBinding.editTextEventLocation.setText(event.eventLocation)

        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        val eventTypeDropdown = bottomBinding.eventTypeMenu // Use ViewBinding
        val eventTypes = resources.getStringArray(R.array.event_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes)

        eventTypeDropdown.setAdapter(adapter)

        createTypePicker()


        // Getting the reference to the date picker UI element
        dateRangeField = bottomBinding.editTextEventDate


        //Sets up the type picker dropdown menu
        //createTypePicker()
        //Listener for user interaction in the `Add Event ` button.

        // Sets up the DatePicker
        DateRangePicker()

        createEvent()

        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        if(bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


    }
    /*override fun onSaveInstanceState(outState: Bundle) {

            bottomBinding.apply {

                    outState.putString(eventName.toString(), textFieldEventName.editText.toString())

                outState.putString(eventLocation.toString(),textFieldEventLocation.editText.toString() )
                outState.putString(eventDate.toString(), textFieldEventDate.editText.toString())
                outState.putString(eventType, textFieldEventType.editText.toString())
                outState.putString(eventDescription.toString(), textFieldEventDescription.editText.toString())
            }

            super.onSaveInstanceState(outState)
            Log.d(TAG, "onSaveInstanceState() method called.")
        } */

    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        dialog?.let {
             bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                 behavior.halfExpandedRatio = 0.5f

                 // Start i half-expanded
                 // NB: STATE_HALF_EXPANDED kræver typisk, at du sætter `setHalfExpandedRatio()`
                 behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Gør den "draggable" (brugeren kan swipe op).
                behavior.isDraggable = true

                // Gør, at brugeren kan komme fra kollapset til fuld expanded
                behavior.skipCollapsed = false


                // For debugging kan du evt. sætte en callback på
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_COLLAPSED ->
                                Log.d("BS", "BottomSheet er kollapset")
                            BottomSheetBehavior.STATE_EXPANDED ->
                                Log.d("BS", "BottomSheet er fuldt udvidet")
                            BottomSheetBehavior.STATE_HALF_EXPANDED ->
                                Log.d("BS", "BottomSheet er i halv tilstand")
                            else -> Unit
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Kan bruges til animationer / lyt til offset
                    }
                })
            }
        }
    }

    /**
     * Sets up the listener for the "Add Event" button to capture user inputs,
     * and updates the event object.
     */
    private fun createEvent() {

        //Initializes the user inputs as variables
        bottomBinding.fabAddEvent.setOnClickListener { view ->
            val eventName = bottomBinding.editTextEventName.text.toString()
            val eventLocation = bottomBinding.editTextEventLocation.text.toString()
            val eventDate = bottomBinding.editTextEventDate.text.toString()
            val eventDescription = bottomBinding.editTextEventDiscription.text.toString()

            if (eventName.isNotEmpty() && eventLocation.isNotEmpty()) {
                // Update the object attributes.
                event.eventName = eventName
                event.eventLocation = eventLocation
                event.eventDate = eventDate
                event.eventType = eventType
                event.eventDescription = eventDescription
                // Calls the Snackbar so it gets shown when the button is clicked
                Snackbar(view)
                //Log the created event
                Log.d(TAG, "Event created ${event}")

            }

        }

    }
    /**
     * function takes a view and creates a snackbar with a message for when events are created.
     *
     * @parem view the current view
     */
    fun Snackbar(view: View) {
        com.google.android.material.snackbar.Snackbar.make(view, "Event added using \n ${event}", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
    }

    /**
     * Configures the dropdown menu for selecting an event type.
     */
    private fun createTypePicker() {
        val eventTypeMenu = bottomBinding.eventTypeMenu
        val eventTypes = resources.getStringArray(R.array.event_types)

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventTypes)
        eventTypeMenu.setAdapter(adapter)

        // Handle item selection
        eventTypeMenu.setOnItemClickListener { adapterView, _, position, _ ->
            eventType = adapterView.getItemAtPosition(position) as String
        }
    }


    /**
     * function creates a pop-up window with a calendar when the choose date field is clicked
     * this date range picker is taken from material components
     * "https://github.com/material-components/material-components-android/blob/master/docs/components/DatePicker.md"
     * When choosing a range of dates, the method will return this ranges in the event date field
     */
    fun DateRangePicker() {
        //Checks todays date to make the calendar starts at today. And to constrain the calendar from beginning to end of the year.
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calender = Calendar.getInstance(TimeZone.getFrozenTimeZone("UTC"))

        calender.timeInMillis = today
        calender[Calendar.MONTH] = Calendar.JANUARY
        val janThisYear = calender.timeInMillis

        calender.timeInMillis = today
        calender[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calender.timeInMillis

        //The constraintbuilder sets the point we start at and that we can only choose dates later than today
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(janThisYear)
            .setEnd(decThisYear)
            .setValidator(DateValidatorPointForward.now())

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getText(R.string.event_date))

            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        // Sets up a click listener so the calendar prompt appears when accessing the field.
        dateRangeField.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "date_range_picker")
        }

        /** Sets up a click listener that arranges the dates in the correct order
         * and saves the values to the event date field
         */
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            // The value returned when the user have chosen the dates and clicked save
            val (startDate, endDate) = selection

            // Formatting the date
            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val startString = format.format(startDate)
            val endString = format.format(endDate)
            val string: String = getString(R.string.date_range, startString, endString)


            // setting the text field  with a start date and an end date
            dateRangeField.setText(string)

        }


    }



    companion object {
    const val TAG = "ModalBottomSheet"
    }


}

