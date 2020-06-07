package com.designbyark.layao.ui.signupdetail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.databinding.FragmentSignUpDetailsBinding
import com.designbyark.layao.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpDetailsFragment : Fragment() {

    private val args: SignUpDetailsFragmentArgs by navArgs()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userCollection: CollectionReference
    private lateinit var binding: FragmentSignUpDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up_details, container, false)
        binding.detail = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection(USERS_COLLECTION)
    }

    private fun updateUserInfo(firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid).set(args.user)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    findNavController().navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
                } else {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    Log.e(LOG_TAG, "Cannot update user info")
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                return@addOnFailureListener
            }
    }

    fun skip() {
        findNavController().navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
    }

    fun completeRegistration() {
        // Disable Interaction
        disableInteraction(requireActivity(), binding.mIncludeProgressBar)

        // Validate fields
        val phoneNumber = binding.mContactET.text.toString().trim()
        val houseNumber = binding.mHouseNumET.text.toString().trim()

        if (phoneValidation(phoneNumber, binding.mContactIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }
        if (emptyValidation(houseNumber, binding.mHouseNumIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        if (binding.mBlockSpinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG)
                .show()
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        // Set values
        args.user.houseNumber = houseNumber
        args.user.blockNumber = binding.mBlockSpinner.selectedItemPosition
        args.user.completeAddress = String.format(
            "House #%s, %s, Wapda Town, Lahore",
            houseNumber,
            binding.mBlockSpinner.selectedItem.toString()
        )
        args.user.gender = binding.mGenderSpinner.selectedItemPosition
        args.user.contact = phoneNumber

        // Start Registration Process
        Log.d(LOG_TAG, "saveUserAtFirestore (Done): Started")
        updateUserInfo(firebaseAuth.currentUser!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.show()
        }
    }

}
