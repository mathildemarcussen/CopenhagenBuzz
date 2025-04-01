package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.EventRowItemBinding

//data class Event(val title: String, val date: String, val location: String, val type: String, val description: String)

class EventAdapter(context: Context, events: List<Event>) : ArrayAdapter<Event>(context, R.layout.event_row_item, events){

    private lateinit var binding: EventRowItemBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            binding = EventRowItemBinding.inflate(inflater, parent, false)
            view = binding.root

            holder = ViewHolder(view)

            view.tag = holder

        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }


        val event = getItem(position)

        holder.eventTitle.text = event?.eventName
        holder.eventType.text = event?.eventType
        holder.eventDate.text = event?.eventDate
        holder.eventLocation.text = event?.eventLocation
        holder.eventDescription.text = event?.eventDescription

        return view
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