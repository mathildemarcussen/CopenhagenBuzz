package dk.itu.moapd.copenhagenbuzz.msem

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentModalBottomSheetListDialogItemBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentModalBottomSheetListDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ModalBottomSheetFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentModalBottomSheetListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_modal_bottom_sheet_list_dialog_item, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.findViewById<RecyclerView>(R.id.list)?.layoutManager =
            LinearLayoutManager(context)
        activity?.findViewById<RecyclerView>(R.id.list)?.adapter =
            arguments?.getInt(ARG_ITEM_COUNT)?.let { ItemAdapter(it) }
    }

    private inner class ViewHolder internal constructor(binding: FragmentModalBottomSheetListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val text: TextView = binding.text
    }

    private inner class ItemAdapter internal constructor(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentModalBottomSheetListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = position.toString()
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }
    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}