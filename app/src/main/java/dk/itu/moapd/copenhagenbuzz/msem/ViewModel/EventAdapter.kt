package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.FragmentManager
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.View.EditFragment
import dk.itu.moapd.copenhagenbuzz.msem.database
import dk.itu.moapd.copenhagenbuzz.msem.databinding.EventRowItemBinding


class EventAdapter(context: Context, fragmentManager: FragmentManager, events: List<Event>, options: FirebaseListOptions<Event>) :
    FirebaseListAdapter<Event>(options) {
        private val fragmentManager = fragmentManager

    override fun populateView(v: View, model: Event, position: Int) {
        val binding = EventRowItemBinding.bind(v)
        binding.eventImage.setImageResource(R.drawable.nyhavn)
        val deleteButton = v.findViewById<ImageButton>(R.id.delete_icon)
        val editButton = v.findViewById<MaterialButton>(R.id.edit_button)
        val shareButton = v.findViewById<MaterialButton>(R.id.share_button)


        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val eventID = getRef(position).key ?: return

        if (uid == model.userID && uid != null) {
            deleteButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
            editButton.visibility = View.GONE
        }

        if (!model.photourl.isNullOrBlank()) {
            Picasso.get()
                .load(model.photourl)
                .into(binding.eventImage)
        }

        deleteButton.setOnClickListener {
            deleteEvent(eventID)
        }

        shareButton.setOnClickListener{
            shareEvent(model)
        }

        editButton.setOnClickListener {

            EditFragment(model, eventID)
                .show(fragmentManager, "custom_dialog_tag")
        }


        if (uid != null) {
            favoriteButtonFunctionality(v, eventID, uid)
        } else {
            v.findViewById<ImageButton>(R.id.lFavorite_icon).visibility = View.GONE
        }

        val event = getItem(position)

        binding.eventName.text = event.eventName
        binding.eventType.text = event?.eventType
        binding.eventDate.text = event?.eventDate
        binding.eventLocation.text = event?.eventLocation?.address
        binding.eventDescription.text = event?.eventDescription
    }

    private fun favoriteButtonFunctionality(v: View, eventID: String, uid: String) {
        val favoriteButton = v.findViewById<ImageButton>(R.id.lFavorite_icon)


        val favoritesRef = database
            .child("favorites")
            .child(uid)


        // Listen for current favorite status for this event
        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(parentSnap: DataSnapshot) {

                var isLiked =
                    parentSnap.hasChild(eventID)
                setFavoriteIcon(favoriteButton, isLiked)

                favoriteButton.setOnClickListener {
                    if (isLiked) {
                        removeFromFavorite(eventID)
                        setFavoriteIcon(favoriteButton, false)
                        isLiked = false
                        Log.d("Favorite", "Removed from favorites")
                    } else {
                        addToFavorite(eventID)
                        setFavoriteIcon(favoriteButton, true)
                        isLiked = true
                        Log.d("Favorite", "Added to favorites")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addToFavorite(event: String) {

        val auth = FirebaseAuth.getInstance()

        auth.currentUser?.let { user ->
            val eventRef = database
                .child("favorites")
                .child(user.uid)
                .child(event)

            eventRef.setValue(event)


        }
    }

    private fun removeFromFavorite(event: String) {
        val auth = FirebaseAuth.getInstance()

        auth.currentUser?.let { user ->
            val eventRef = database
                .child("favorites")
                .child(user.uid)
                .child(event)

            eventRef.removeValue()
        }
    }

    private fun deleteEvent(eventID: String) {
        val auth = FirebaseAuth.getInstance()

        auth.currentUser?.let { user ->
            val eventRef = database
                .child("events")
                .child(eventID)

            eventRef.removeValue()
        }

    }

    private fun shareEvent(event: Event) {
        val context = fragmentManager.fragments.firstOrNull()?.context ?: return

        val shareText = """
        Check out this event!
        
        Name: ${event.eventName}
        Type: ${event.eventType}
        Date: ${event.eventDate}
        Location: ${event.eventLocation?.address}
        Description: ${event.eventDescription}
    """.trimIndent()

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    private fun setFavoriteIcon(favoriteButton: ImageButton, isLiked: Boolean) {
        if (isLiked) {
            // Change icon to "liked" state (e.g., filled heart)
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            // Change icon to "not liked" state (e.g., border heart)
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }
}