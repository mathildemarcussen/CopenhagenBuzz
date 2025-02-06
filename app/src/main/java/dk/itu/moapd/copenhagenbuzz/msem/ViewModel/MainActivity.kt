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
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentMainBinding
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
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
    private lateinit var eventType: String
    private lateinit var eventDescription : EditText


    /**
     * Instantiation of an object of the `Event ` class.
     * which takes the input eventName, eventLocation, eventDate, eventType and eventDescription
     */
    private val event: Event = Event("", "","", "", "")

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

        //Sets up the type picker dropdown menu
        createTypePicker()
        //Listener for user interaction in the `Add Event ` button.
        createEvent()

    }

    /**
     * Sets up the listener for the "Add Event" button to capture user inputs,
     * and updates the event object.
     */
    private fun createEvent() {
        //Initializes the user inputs as variables
        customBinding.fabAddEvent.setOnClickListener {
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
        val items = listOf("Party", "Conference", "Foood")

        val eventTypeMenu =
            findViewById<AutoCompleteTextView>(R.id.event_type_menu)

        // Set up the dropdown adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        eventTypeMenu.setAdapter(adapter)

        // Handle item selection from the dropdown
        customBinding.eventTypeMenu.setOnItemClickListener { adapterView, _, position, _ ->
                    val selectedType = adapterView.getItemAtPosition(position) as String
                    eventType = selectedType
                }
    }

}

