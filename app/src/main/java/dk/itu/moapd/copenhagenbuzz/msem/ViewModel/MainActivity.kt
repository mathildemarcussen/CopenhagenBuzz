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
    * ViewBindings to make the interaction between the code and our views becomes eaiser.
    */
    private lateinit var binding: ActivityMainBinding
    private lateinit var customBinding: ContentMainBinding


    companion object {
        private val TAG = MainActivity::class.qualifiedName
        }

    /**
     * A set of private constants used in the class.
     */
    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventType: String
    private lateinit var eventDescription : EditText


    /**
     * An instance of the `Event ` class.
     */
    private val event: Event = Event("", "","", "", "")


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customBinding = ContentMainBinding.inflate(layoutInflater)
        setContentView(customBinding.root)

        createTypePicker()

        customBinding.autoCompleteEventType.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedType = adapterView.getItemAtPosition(position) as String
            eventType = selectedType // Gem streng i event
        }


        // Listener for user interaction in the `Add Event ` button.
        customBinding.fabAddEvent.setOnClickListener {
                createEvent()
        }
    }

    private fun createEvent() {
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
        }

    }

    private fun createTypePicker() {
        val items = listOf("Party", "Conference", "Foood")

        val autoCompleteEventType =
            findViewById<AutoCompleteTextView>(R.id.auto_complete_event_type)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        autoCompleteEventType.setAdapter(adapter)
    }

}

