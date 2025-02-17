/*
* MIT License
*
* Copyright (c) [year] [fullname]
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentMainBinding
import androidx.core.view.WindowCompat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.View.LoginActivity
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.MainActivity.Companion.TAG


/**
 * Activity class with methods that manage the  main activities of the CopenhagenBuzz app
 */
 class MainActivity : AppCompatActivity() {

    /**
     * ViewBindings used to make the interaction between the code and our views easier.
     */
    private lateinit var binding: ActivityMainBinding
    private lateinit var customBinding: ContentMainBinding

    /**
     * The companion object defines class level functions,
     * in this one we set TAG that is used when logging something in logcat.
     * The TAG shows in logcat wherefrom the log came. This one sets it as
     * coming from MainAcrivity.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * A set of private variables used in the class.
     */
    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventDate: EditText
    private lateinit var dateRangeField: TextInputEditText
    private lateinit var eventType: String
    private lateinit var eventDescription: EditText
    public var isLoggedIn: Boolean = false



    /**
     * Instantiation of an object of the `Event ` class.
     * which takes the input eventName, eventLocation, eventDate, eventType and eventDescription
     */
    private val event: Event = Event("", "", "", "", "")

    /**
     * Called when activity is starting.Initializes UI elements and event listeners.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customBinding = ContentMainBinding.inflate(layoutInflater)
        setContentView(customBinding.root)

        // Getting the reference to the date picker UI element
        dateRangeField = findViewById(R.id.edit_text_event_date)

        //Sets up the type picker dropdown menu
        createTypePicker()
        //Listener for user interaction in the `Add Event ` button.
        createEvent()

        // Sets up the DatePicker
        DateRangePicker()

        val userButton = findViewById<ImageButton>(R.id.login)

        isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        updateUserIcon(userButton)

        userButton.setImageResource(if (isLoggedIn) R.drawable.baseline_logout_24 else R.drawable.baseline_account_circle_24)

        userButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        updateUserIcon(userButton)


    }

    private fun updateUserIcon(userButton: ImageButton) {
        if (isLoggedIn) {
            userButton.setImageResource(R.drawable.baseline_logout_24) // Logout icon
        } else {
            userButton.setImageResource(R.drawable.baseline_account_circle_24) // Login icon
        }
    }

    /**
     * Sets up the listener for the "Add Event" button to capture user inputs,
     * and updates the event object.
     */
    private fun createEvent() {
        //Initializes the user inputs as variables
        customBinding.fabAddEvent.setOnClickListener { view ->
            eventName = findViewById(R.id.edit_text_event_name)
            eventLocation = findViewById(R.id.edit_text_event_location)
            eventDate = findViewById(R.id.edit_text_event_date)
            eventDescription = findViewById(R.id.edit_text_event_discription)

            if (eventName.text.toString().isNotEmpty() &&
                eventLocation.text.toString().isNotEmpty()
            ) {
                // Update the object attributes.
                event.eventName = eventName.text.toString().trim()
                event.eventLocation = eventLocation.text.toString().trim()
                event.eventDate = eventDate.text.toString().trim()
                event.eventType = eventType
                event.eventDescription = eventDescription.text.toString().trim()
                // Calls the Snackbar so it gets shown when the button is clicked
                Snackbar(view)
                //Log the created event
                Log.d(TAG, "Event created ${event}")

            }
        }

    }

    /**
     * Configures the dropdown menu for selecting an event type.
     */
    private fun createTypePicker() {
        //Lists of event types available in the drop down menu
        val array: Array<String> = resources.getStringArray(R.array.event_types)

        val eventTypeMenu =
            findViewById<AutoCompleteTextView>(R.id.event_type_menu)

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
        eventTypeMenu.setAdapter(adapter)

        // Handle item selection from the dropdown
        customBinding.eventTypeMenu.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedType = adapterView.getItemAtPosition(position) as String
            eventType = selectedType
        }
    }

    /**
     * function takes a view and creates a snackbar with a message for when events are created.
     *
     * @parem view the current view
     */
    fun Snackbar(view: View) {
        Snackbar.make(view, "Event added using \n ${event}", Snackbar.LENGTH_LONG).show()
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
            dateRangePicker.show(supportFragmentManager, "date_range_picker")
        }

        /** Sets up a click listener that arranges the dates in the correct order
         * and saves the values to the event date field
         */
        dateRangePicker.addOnPositiveButtonClickListener{selection ->
            // The value returned when the user have chosen the dates and clicked save
            val (startDate, endDate) = selection

            // Formatting the date
            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val startString = format.format(startDate)
            val endString   = format.format(endDate)
            val string: String = getString(R.string.date_range, startString, endString)



            // setting the text field  with a start date and an end date
            dateRangeField.setText(string)

        }



    }
}

