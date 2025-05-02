package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventViewModel
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.FavoriteEventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.database
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentFavoritesBinding


/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private lateinit var adapter: FavoriteEventAdapter
    private val eventViewModel: EventViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // Initialiser adapter med en tom liste
        adapter = FavoriteEventAdapter(emptyList())

        // Sæt adapter og layoutManager på RecyclerView
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteRecyclerView.adapter = adapter

        // Observer ViewModel's favorites-liste
        eventViewModel._favorites.observe(viewLifecycleOwner) { favoriteEvents ->
            adapter.updateData(favoriteEvents ?: emptyList())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchFavorites()
    }

    /**
     * Fetches the user's favorite event IDs from the Firebase Realtime Database,
     * then retrieves the full event data for each ID from the main events table.
     *
     * The full `Event` objects are collected and used to update the adapter.
     * This allows the favorites list to display full event information, even
     * though the favorites table only stores references (event IDs).
     */
    private fun fetchFavorites() {
        // Get the currently authenticated user
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return


        // Reference to the user's list of favorite event IDs
        val favoritesRef = database
            .child("CopenhagenBuzz")
            .child("favorites")
            .child(userId)

        // Reference to the main events table containing all full Event objects
        val eventsRef = database
            .child("CopenhagenBuzz")
            .child("events")

        // Get the list of favorite event IDs
        favoritesRef.get().addOnSuccessListener { snapshot ->
            // Extract event IDs from the snapshot (keys of the children)
            val favoriteIds = snapshot.children.mapNotNull { it.key }

            // Temporary list to hold the actual Event objects
            val favoriteEvents = mutableListOf<Event>()

            // Counter to track when all asynchronous calls have completed
            var remaining = favoriteIds.size

            // If no favorites, update adapter immediately with an empty list
            if (favoriteIds.isEmpty()) {
                adapter.updateData(emptyList())
                return@addOnSuccessListener
            }

            // For each favorite event ID, retrieve the full Event object
            for (eventId in favoriteIds) {
                eventsRef.child(eventId).get().addOnSuccessListener { eventSnapshot ->
                    // Convert the snapshot into an Event object
                    val event = eventSnapshot.getValue(Event::class.java)

                    // Add it to the list if not null
                    if (event != null) {
                        favoriteEvents.add(event)
                    }

                    // When all events are loaded, update the adapter
                    remaining--
                    if (remaining == 0) {
                        adapter.updateData(favoriteEvents)
                    }

                }.addOnFailureListener {
                    // If a single event fetch fails, still count it down
                    remaining--
                    if (remaining == 0) {
                        adapter.updateData(favoriteEvents)
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}