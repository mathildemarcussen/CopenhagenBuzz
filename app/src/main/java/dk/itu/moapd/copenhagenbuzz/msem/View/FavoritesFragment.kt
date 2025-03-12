package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.DataViewModel
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.FavoriteEventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentFavoritesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentFavoritesBinding? = null
    private lateinit var adapter: FavoriteEventAdapter
    private val viewModel: DataViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    /*fun onCreatedView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // Initialiser adapter med en tom liste
        adapter = FavoriteEventAdapter(emptyList())

        // Sæt adapter og layoutManager på RecyclerView
        binding.favoriteListView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteListView.adapter = adapter

        // Observer ViewModel's favorites-liste
        viewModel.favorites.observe(viewLifecycleOwner) { favoriteEvents ->
            adapter.updateData(favoriteEvents)
        }

        return binding.root
    } */

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
        viewModel._favorites.observe(viewLifecycleOwner) { favoriteEvents ->
            adapter.updateData(favoriteEvents ?: emptyList())
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply{
            val data = listOf(
                Event(
                    eventName = "Copoaw",
                    eventLocation = "Copenhagen",
                    eventDate = "22 februar",
                    eventType = "party :))",
                    eventDescription = "aaaaaaaaaaaaaa"
                ),
                Event(
                    eventName = "Copenhagen Light Festival",
                    eventLocation = "Copenhagen",
                    eventDate = "1 marts",
                    eventType = "Festival",
                    eventDescription = "Beautiful light instalations all over Copenahgen"
                ),
                Event(
                    eventName = "Tate Mcrae",
                    eventLocation = "Royal Arena",
                    eventDate = "30 maj",
                    eventType = "concert",
                    eventDescription = "Pop Concert"
                )
            )

            val adapter = FavoriteEventAdapter(data)
            binding.favoriteRecyclerView.adapter = adapter
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}