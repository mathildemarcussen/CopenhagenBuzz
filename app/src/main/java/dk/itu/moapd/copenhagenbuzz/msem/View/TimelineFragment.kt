package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventViewModel
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentTimelineBinding



/**
 * A simple [Fragment] subclass.
 * Use the [TimelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimelineFragment : Fragment() {
    private var _binding: FragmentTimelineBinding? = null
    private var isLiked = false
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val favoriteButton = binding.root.findViewById<ImageButton>(R.id.lFavorite_icon)

        favoriteButton.setOnClickListener{
            if (isLiked) {
                isLiked = false

            } else {
                val eventTitle = favoriteButton.contentDescription.toString()
                isLiked = true
                addToFavorite(eventTitle)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val query = Firebase.database(DATABASE_URL).reference
                .child("CopenhagenBuzz")
                .child("events")
                .orderByChild("eventDate")


            val options = FirebaseListOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLayout(R.layout.event_row_item)
                .setLifecycleOwner(this)
                .build()

            binding.listView.adapter = EventAdapter(requireContext(), emptyList(), options)

        }

    }

    fun addToFavorite(event: String) {

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}