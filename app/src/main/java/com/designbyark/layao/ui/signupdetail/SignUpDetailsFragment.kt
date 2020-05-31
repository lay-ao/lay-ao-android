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
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentSignUpDetailsBinding
import com.designbyark.layao.ui.signup.SignUpFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpDetailsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var user: User? = null

    private lateinit var userCollection: CollectionReference
    private lateinit var binding: FragmentSignUpDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(SignUpFragment.KEY) as User
        }
    }

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
            .set(user!!)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    sendVerificationEmail()

                } else {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    Log.e(
                        LOG_TAG,
                        "saveUserAtFirestore -> addOnCompleteListener: task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
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
        disableInteraction(requireActivity(), binding.mIncludeProgressBar)

        // Set Default values
        user?.houseNumber = ""
        user?.blockNumber = 0
        user?.completeAddress = ""
        user?.gender = 0
        user?.contact = ""
        user?.fineCount = 0

        // Store user at Firestore
        Log.d(LOG_TAG, "saveUserAtFirestore (Skip): Started")
        saveUserAtFirestore(firebaseAuth.currentUser!!)
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
        user?.houseNumber = houseNumber
        user?.blockNumber = binding.mBlockSpinner.selectedItemPosition
        user?.completeAddress = String.format(
            "House #%s, %s, Wapda Town, Lahore",
            houseNumber,
            binding.mBlockSpinner.selectedItem.toString()
        )
        user?.gender = binding.mGenderSpinner.selectedItemPosition
        user?.contact = phoneNumber
        user?.fineCount = 0

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
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    findNavController().navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
                }
            }?.addOnFailureListener { exception ->
                Log.e(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            }
    }

}
