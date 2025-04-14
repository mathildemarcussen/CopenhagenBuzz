package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.EventRowItemBinding


class EventAdapter(context: Context, events: List<Event>, options: FirebaseListOptions<Event>) : FirebaseListAdapter<Event>(options) {

    private lateinit var binding: EventRowItemBinding
    private var _context = context
    private var isLiked = false


    override fun populateView(v: View, model: Event, position: Int) {
        val binding =  EventRowItemBinding.bind(v)
        val favoriteButton = v.findViewById<ImageButton>(R.id.lFavorite_icon)

        favoriteButton.setOnClickListener{
            setFavoriteIcon(favoriteButton, isLiked)
            if (isLiked) {
                isLiked = false
                removeFromFavorite(v.findViewById<TextView>(R.id.event_name).toString())
                Log.d("Favorite", "Removed from favorites")

            } else {
                isLiked = true
                addToFavorite(v.findViewById<TextView>(R.id.event_name).toString())
                Log.d("Favorite", "Added to favorites")

            }
        }

        val event = getItem(position)

        binding.eventName.text = event.eventName
        binding.eventType.text = event?.eventType
        binding.eventDate.text = event?.eventDate
        binding.eventLocation.text = event?.eventLocation
        binding.eventDescription.text = event?.eventDescription
    }

    private fun addToFavorite(event: String) {

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(DATABASE_URL).reference
        val objectType = "default"

        auth.currentUser?.let { user ->
            val eventRef = database
                .child("CopenhagenBuzz")
                .child("favorites")
                .child(auth.currentUser?.uid.toString())
                .push()

            eventRef.setValue(event)



        }
    }
    private fun removeFromFavorite(event: String) {
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(DATABASE_URL).reference

        auth.currentUser?.let { user ->
                val eventRef = database
                    .child("CopenhagenBuzz")
                    .child("favorites")
                    .child(auth.currentUser?.uid.toString())



            val query = eventRef.orderByValue().equalTo(event)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        }
    }

    private fun setFavoriteIcon(favoriteButton: ImageButton, isLiked: Boolean) {
        if (!isLiked) {
            // Change icon to "liked" state (e.g., filled heart)
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            // Change icon to "not liked" state (e.g., border heart)
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }


}