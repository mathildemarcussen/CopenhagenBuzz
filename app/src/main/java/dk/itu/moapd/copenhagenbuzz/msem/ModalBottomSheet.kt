package dk.itu.moapd.copenhagenbuzz.msem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_content, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
    super.onViewCreated(view, savedInstanceState)

    val bottomSheetDialog = dialog as? BottomSheetDialog
    val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

    if(bottomSheet != null) {
    val behavior = BottomSheetBehavior.from(bottomSheet)
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    }

    companion object {
    const val TAG = "ModalBottomSheet"
    }


}

