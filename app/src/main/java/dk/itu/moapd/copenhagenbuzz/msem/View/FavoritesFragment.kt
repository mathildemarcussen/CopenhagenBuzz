package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.DataViewModel
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventViewModel
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.FavoriteEventAdapter
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

        eventViewModel.events.observe(viewLifecycleOwner) { events ->
                    binding.favoriteRecyclerView.adapter =
                        FavoriteEventAdapter(
                            events
                        )
                }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}