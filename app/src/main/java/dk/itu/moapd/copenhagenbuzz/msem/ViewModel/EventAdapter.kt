package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.EventRowItemBinding


class EventAdapter(context: Context, events: List<Event>, options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    private lateinit var binding: EventRowItemBinding
    private var _context = context

    override fun populateView(v: View, model: Event, position: Int) {
        //val inflater = LayoutInflater.from(_context)
        //val view: View
        val binding =  EventRowItemBinding.bind(v)
        //val holder: ViewHolder


        val event = getItem(position)

        binding.eventName.text = event.eventName
        binding.eventType.text = event?.eventType
        binding.eventDate.text = event?.eventDate
        binding.eventLocation.text = event?.eventLocation
        binding.eventDescription.text = event?.eventDescription
    }



    inner class ViewHolder(view: View) {
        val eventTitle: TextView = view.findViewById(R.id.event_name)
        val eventType: TextView = view.findViewById(R.id.event_type)
        val eventDate: TextView = view.findViewById(R.id.event_date)
        val eventLocation: TextView = view.findViewById(R.id.event_location)
        val eventDescription: TextView = view.findViewById(R.id.event_description)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val infoButton: Button = view.findViewById(R.id.info_button)
    }

}