package com.designbyark.layao.ui.signupdetail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private fun saveUserAtFirestore(firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid)
            .set(args.user)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    sendVerificationEmail()

                } else {
                    enableInteraction(
                        requireActivity(),
                        binding.mIncludeProgressBar
                    )
                    Log.e(
                        LOG_TAG,
                        "saveUserAtFirestore -> addOnCompleteListener: task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(
                    requireActivity(),
                    binding.mIncludeProgressBar
                )
                Log.e(
                    LOG_TAG,
                    "saveUserAtFirestore -> addOnFailureListener: ${it.localizedMessage}",
                    it
                )
                return@addOnFailureListener
            }
    }

    fun skip() {
        // Disable Interaction
        disableInteraction(
            requireActivity(),
            binding.mIncludeProgressBar
        )

        // Set Default values
        args.user.houseNumber = ""
        args.user.blockNumber = 0
        args.user.completeAddress = ""
        args.user.gender = 0
        args.user.contact = ""

        // Store user at Firestore
        Log.d(LOG_TAG, "saveUserAtFirestore (Skip): Started")
        saveUserAtFirestore(firebaseAuth.currentUser!!)
    }

    fun completeRegistration() {
        // Disable Interaction
        disableInteraction(
            requireActivity(),
            binding.mIncludeProgressBar
        )

        // Validate fields
        val phoneNumber = binding.mContactET.text.toString().trim()
        val houseNumber = binding.mHouseNumET.text.toString().trim()

        if (phoneValidation(
                phoneNumber,
                binding.mContactIL
            )
        ) {
            enableInteraction(
                requireActivity(),
                binding.mIncludeProgressBar
            )
            return
        }
        if (emptyValidation(
                houseNumber,
                binding.mHouseNumIL
            )
        ) {
            enableInteraction(
                requireActivity(),
                binding.mIncludeProgressBar
            )
            return
        }

        if (binding.mBlockSpinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG)
                .show()
            enableInteraction(
                requireActivity(),
                binding.mIncludeProgressBar
            )
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
        saveUserAtFirestore(firebaseAuth.currentUser!!)
    }

    private fun sendVerificationEmail() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    Log.d(LOG_TAG, "Email Sent")
                    enableInteraction(
                        requireActivity(),
                        binding.mIncludeProgressBar
                    )
                    findNavController().navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
                }
            }?.addOnFailureListener { exception ->
                Log.e(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(
                    requireActivity(),
                    binding.mIncludeProgressBar
                )
            }
    }

}
