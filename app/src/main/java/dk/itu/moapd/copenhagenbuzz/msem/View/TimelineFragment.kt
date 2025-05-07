package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.database
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentTimelineBinding

class TimelineFragment : Fragment() {
    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = database
            .child("events")
            .orderByChild("eventDate")


        val options = FirebaseListOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .setLayout(R.layout.event_row_item)
            .setLifecycleOwner(this)
            .build()

        binding.listView.adapter =
            EventAdapter(requireContext(), parentFragmentManager, emptyList(), options)
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}