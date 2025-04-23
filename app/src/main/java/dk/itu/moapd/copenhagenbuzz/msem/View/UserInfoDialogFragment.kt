package dk.itu.moapd.copenhagenbuzz.msem.View

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dk.itu.moapd.copenhagenbuzz.msem.R
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.DialogFragment
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentUserInfoDialogBinding

class UserInfoDialogFragment : DialogFragment() {
    private var _binding: FragmentUserInfoDialogBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Inflate the view using view binding.
        _binding = FragmentUserInfoDialogBinding.inflate(layoutInflater)

        val currentUser = FirebaseAuth.getInstance().currentUser

        // Populate the dialog view with user information.
        currentUser?.let { user ->
            with(binding) {
                textViewName.text = user.displayName ?: getString(R.string.unknown_user)
                textViewEmail.text = user.email ?: user.phoneNumber
            }
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.user_info_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info_dialog, container, false)
    }




}