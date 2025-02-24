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
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.component1
import androidx.core.util.component2
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dk.itu.moapd.copenhagenbuzz.msem.ModalBottomSheet
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.View.LoginActivity
import dk.itu.moapd.copenhagenbuzz.msem.databinding.BottomSheetContentBinding


/**
 * Activity class with methods that manage the  main activities of the CopenhagenBuzz app
 */
class MainActivity : AppCompatActivity() {

    /**
     * ViewBindings used to make the interaction between the code and our views easier.
     */
    private lateinit var gestureDetector: GestureDetector
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

    private lateinit var dateRangeField: TextInputEditText
    private lateinit var eventType: String
    var isLoggedIn: Boolean = false


    /**
     * Instantiation of an object of the `Event ` class.
     * which takes the input eventName, eventLocation, eventDate, eventType and eventDescription
     */

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

        // Getting the reference to the date picker UI element
        dateRangeField = findViewById(R.id.edit_text_event_date)

        //Sets up the type picker dropdown menu
        //createTypePicker()
        //Listener for user interaction in the `Add Event ` button.

        // Sets up the DatePicker
        DateRangePicker()

        // Find and sssigns a reference to the imagebutton
        val userButton = findViewById<ImageButton>(R.id.login)

        // Retrieves the boolean value from the LoginActivity wether it is true or false
        isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        // Updates the userbutton based on the isLoggedIn value
        updateUserIcon(userButton)

        /** Click lisnetner for the User button
         * Calls startActivity to launch LoginActivity
         * Calls finish to close MainActivity
         */
        userButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    // Respond to navigation item 1 click
                    true
                }

                R.id.item_2 -> {
                    // Respond to navigation item 2 click
                    true
                }

                R.id.item_3 -> {
                    true
                }

                R.id.item_4 -> {
                    true
                }

                else -> false
            }
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    val deltaY = e2.y - e1.y
                    if (deltaY < -100) { // Swipe up detected
                        val myBottomSheet = ModalBottomSheet()
                        myBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
                        return true
                    }
                }
                return false            }
        })



        val swipeArea = findViewById<View>(R.id.swipeArea)
        swipeArea.setOnTouchListener{_, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        val myBottomSheet = ModalBottomSheet()
        myBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)

        val bottomSheet: View = findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Initially hide it
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> Log.d(TAG, "Bottom Sheet Expanded")
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.d(TAG, "Bottom Sheet Collapsed")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.d(TAG, "Bottom Sheet Hidden")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding effects if needed
            }
        })
    }

    /**
     * This Method determines which pivture is shown for the icon wether it is a
     * logout icon our a guest symbol
     */
    private fun updateUserIcon(userButton: ImageButton) {
        if (isLoggedIn) {
            userButton.setImageResource(R.drawable.baseline_logout_24) // Logout icon
        } else {
            userButton.setImageResource(R.drawable.baseline_account_circle_24) // Login icon
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
            dateRangePicker.show(supportFragmentManager, "date_range_picker")
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

