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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var customBinding: ContentMainBinding

    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        }

    // GUI variables.

    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var eventDate: EditText
    private lateinit var eventType: EditText
    private lateinit var eventDescription : EditText


    // An instance of the `Event ` class.
    private val event: Event = Event("", "","", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customBinding = ContentMainBinding.inflate(layoutInflater)
        setContentView(customBinding.root)

        // Link the UI components with the Kotlin source -code.
        eventName = findViewById(R.id.edit_text_event_name)
        eventLocation = findViewById(R.id.edit_text_event_location)
        addEventButton = findViewById(R.id.fab_add_event)
        eventDate = findViewById(R.id.edit_text_event_date)
        eventType = findViewById(R.id.auto_complete_event_type)
        eventDescription = findViewById(R.id.edit_text_event_discription)

        val items = listOf("Party", "Conference", "Foood")

        val autoCompleteEventType =
            findViewById<AutoCompleteTextView>(R.id.auto_complete_event_type)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        autoCompleteEventType.setAdapter(adapter)

        customBinding.autoCompleteEventType.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedType = adapterView.getItemAtPosition(position) as String
            event.setEventType(selectedType) // Gem streng i event
        }


        // Listener for user interaction in the `Add Event ` button.
        customBinding.addEventButton.setOnClickListener {

            // Only execute the following code when the user fills all
            // `EditText `.
            if (eventName.text.toString().isNotEmpty() &&
                eventLocation.text.toString().isNotEmpty()
            ) {
            }

            // Update the object attributes.
            event.setEventName(
                eventName.text.toString().trim()
            )
            event.setEventLocation(
                eventLocation.text.toString().trim()
            )

            event.setEventDate(
                eventDate.text.toString().trim()
            )

            event.setEventType(
                eventType.text.toString().trim()
            )

            event.setEventDescription(
                eventDescription.text.toString().trim()
            )

            // Write in the `Logcat ` system.
            showMessage()




            //datePicker.show(supportFragmentManager, "tag")
        }


        /*

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                    .build()

            }

            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Dates")
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                .build()

            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            calendar.timeInMillis = today
            calendar[Calendar.MONTH] = Calendar.JANUARY

            val janThisYear = calendar.timeInMillis

         */
    }
    fun showMessage() {
        Log.d(TAG, event.toString())
    }
    }

