package dk.itu.moapd.copenhagenbuzz.msem.View

import android.app.Dialog
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.Model.EventLocation
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentEditBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentUserInfoDialogBinding

class EditFragment(_event: Event, eventID: String) : DialogFragment() {
    private var _binding: FragmentEditBinding? = null
    private val eventID = eventID
    private var event = _event
    private lateinit var eventType: String
    private lateinit var dateRangeField: TextInputEditText

   private val binding
        get() = requireNotNull(_binding) {
       "Cannot access binding because it is null. Is the view visible?"
   }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database.reference

        _binding = FragmentEditBinding.inflate(layoutInflater)
        val view = binding.root
        val saveButton = view.findViewById<MaterialButton>(R.id.save_button)
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancel_button)

        binding.editTextEventName.setText(event.eventName)
        binding.editTextEventLocation.setText(event.eventLocation.address)
        binding.editTextEventDate.setText(event.eventDate)
        binding.editTextEventDiscription.setText(event.eventDescription)
        binding.eventTypeMenu.setText(event.eventType)

        val eventTypeDropdown = binding.eventTypeMenu // Use ViewBinding
        val eventTypes = resources.getStringArray(R.array.event_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes)



        eventTypeDropdown.setAdapter(adapter)

        createTypePicker()


        // Getting the reference to the date picker UI element
        dateRangeField = binding.editTextEventDate

        saveButton.setOnClickListener {
            Log.d("EditFragment", "Save button clicked")
            val eventName = binding.editTextEventName.text.toString()
            val eventLocation = EventLocation()
            val eventDate = binding.editTextEventDate.text.toString()
            val eventDescription = binding.editTextEventDiscription.text.toString()
            val eventType = binding.eventTypeMenu.text.toString()
            val userID = auth.currentUser?.uid
            val event = Event(eventName, eventLocation, eventDate, eventType, eventDescription, userID)

            auth.currentUser?.let{ user ->
                val eventRef = database
                    .child("CopenhagenBuzz")
                    .child("events")
                    .child(eventID)

                eventRef.setValue(event)
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }

        // Sets up the DatePicker
        DateRangePicker()


        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_event)
            .setView(binding.root)
            .create()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    private fun createTypePicker() {
        val eventTypeMenu = binding.eventTypeMenu
        val eventTypes = resources.getStringArray(R.array.event_types)

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventTypes)
        eventTypeMenu.setAdapter(adapter)

        // Handle item selection
        eventTypeMenu.setOnItemClickListener { adapterView, _, position, _ ->
            eventType = adapterView.getItemAtPosition(position) as String
        }
    }


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

}