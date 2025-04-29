package dk.itu.moapd.copenhagenbuzz.msem.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentCalendarBinding
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCalendarBinding.inflate(inflater, container, false).also {
        // Inflate the layout for this fragment
        _binding = it
    }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}