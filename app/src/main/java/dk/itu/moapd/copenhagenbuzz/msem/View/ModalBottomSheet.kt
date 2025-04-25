package dk.itu.moapd.copenhagenbuzz.msem.View

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.BottomSheetContentBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.msem.Model.EventLocation
import dk.itu.moapd.copenhagenbuzz.msem.Model.LocationService
import dk.itu.moapd.copenhagenbuzz.msem.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomBinding: BottomSheetContentBinding
    private val event: Event = Event("", EventLocation(), "", "", "", "")
    private lateinit var eventType: String
    private lateinit var dateRangeField: TextInputEditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.bottom_sheet_content, container, false)
        bottomBinding = BottomSheetContentBinding.inflate(inflater)
        return bottomBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        bottomBinding.editTextEventName.setText(event.eventName)

        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        val eventTypeDropdown = bottomBinding.eventTypeMenu // Use ViewBinding
        val eventTypes = resources.getStringArray(R.array.event_types)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes)

        eventTypeDropdown.setAdapter(adapter)

        createTypePicker()


        // Getting the reference to the date picker UI element
        dateRangeField = bottomBinding.editTextEventDate


        // Sets up the DatePicker
        DateRangePicker()

        createEvent()

        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


    }


    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

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
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(DATABASE_URL).reference
        val objectType = "default"

        //Initializes the user inputs as variables
        bottomBinding.fabAddEvent.setOnClickListener { view ->
            val eventLocation = bottomBinding.editTextEventLocation.text.toString().trim()
            if (eventLocation.isEmpty()) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address =
                            addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"

                        event.eventLocation = EventLocation(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            address = address
                        )
                    } else {
                        event.eventLocation = EventLocation()
                    }
                }
            } else {
                lifecycleScope.launch {
                    val locationResult = geocodeAddress(
                        requireContext(),
                        bottomBinding.editTextEventLocation.text.toString()
                    )
                    if (locationResult != null) {
                        // Do something with the result (e.g., update the event location)
                        val (latitude, longitude) = locationResult
                        event.eventLocation = EventLocation(latitude, longitude)
                    }
                }

            }

            val eventName = bottomBinding.editTextEventName.text.toString()
            val eventDate = bottomBinding.editTextEventDate.text.toString()
            val eventDescription = bottomBinding.editTextEventDiscription.text.toString()
            val userID = auth.currentUser?.uid.toString() // we know it is bad code okay


            if (eventName.isNotEmpty() && event.eventLocation != null) {
                // Update the object attributes.
                event.eventName = eventName
                event.eventLocation = event.eventLocation
                event.eventDate = eventDate
                event.eventType = eventType
                event.eventDescription = eventDescription
                event.userID = userID
                // Calls the Snackbar so it gets shown when the button is clicked
                Snackbar(view)
                //Log the created event
                Log.d(TAG, "Event created ${event}")

                auth.currentUser?.let { user ->
                    val eventRef = database
                        .child("CopenhagenBuzz")
                        .child("events")
                        .push()

                    eventRef.setValue(event)
                }
            }

        }

    }


    /**
     * function takes a view and creates a snackbar with a message for when events are created.
     *
     * @parem view the current view
     */
    fun Snackbar(view: View) {
        com.google.android.material.snackbar.Snackbar.make(
            view,
            "Event added using \n ${event}",
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    /**
     * Configures the dropdown menu for selecting an event type.
     */
    private fun createTypePicker() {
        val eventTypeMenu = bottomBinding.eventTypeMenu
        val eventTypes = resources.getStringArray(R.array.event_types)

        // Set up the dropdown adapter
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventTypes)
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

    suspend fun geocodeAddress(
        context: Context,
        addressString: String
    ): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            val geo = Geocoder(context, Locale.getDefault())
            // Maks 1 resultat tilbage
            val results = geo.getFromLocationName(addressString, 1)
            if (!results.isNullOrEmpty()) {
                val addr = results[0]
                addr.latitude to addr.longitude
            } else {
                null  // Intet resultat
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null  // Netværksfejl ell. mangel på Geocoder‐service
        }
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }


}

