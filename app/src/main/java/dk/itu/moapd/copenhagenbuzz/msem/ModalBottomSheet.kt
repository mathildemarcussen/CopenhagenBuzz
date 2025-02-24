package dk.itu.moapd.copenhagenbuzz.msem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.compose.material3.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.MainActivity
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.MainActivity.Companion
import dk.itu.moapd.copenhagenbuzz.msem.databinding.BottomSheetContentBinding

class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomBinding: BottomSheetContentBinding
    private val event: Event = Event("", "", "", "", "")
    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventType: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.bottom_sheet_content, container, false)
        return bottomBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
    super.onViewCreated(view, savedInstanceState)
        bottomBinding = BottomSheetContentBinding.inflate(layoutInflater)
        bottomBinding.editTextEventName.setText(event.eventName)
        bottomBinding.editTextEventLocation.setText(event.eventLocation)

        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        val eventTypeDropdown = bottomBinding.eventTypeMenu // Use ViewBinding
        val eventTypes = resources.getStringArray(R.array.event_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes)

        eventTypeDropdown.setAdapter(adapter)

        val bottomSheetDialog = dialog as? BottomSheetDialog
    val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

    if(bottomSheet != null) {
    val behavior = BottomSheetBehavior.from(bottomSheet)
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    }

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



    companion object {
    const val TAG = "ModalBottomSheet"
    }


}

