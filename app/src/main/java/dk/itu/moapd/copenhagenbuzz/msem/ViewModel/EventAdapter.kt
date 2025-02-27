package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

data class Event(val title: String, val date: String)

class EventAdapter(context: Context, events: List<Event>) : ArrayAdapter<Event>(context, 0, events){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        }

        val event = getItem(position)

        val titleTextView = listItemView!!.findViewById<TextView>(android.R.id.text1)
        val dateTextView = listItemView.findViewById<TextView>(android.R.id.text2)

        titleTextView.text = event?.title
        dateTextView.text = event?.date

        return listItemView
    }


    inner class ViewHolder {

    }

}