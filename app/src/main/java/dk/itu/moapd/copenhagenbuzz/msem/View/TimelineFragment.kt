package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventAdapter
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentTimelineBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimelineFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentTimelineBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentTimelineBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


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

            val adapter = EventAdapter(requireContext(), data )
            val listView = view.findViewById<ListView>(R.id.list_view)
            listView.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimelineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}