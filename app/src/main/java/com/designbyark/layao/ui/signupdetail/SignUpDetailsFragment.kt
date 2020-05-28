package com.designbyark.layao.ui.signupdetail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.designbyark.layao.ui.signup.SignUpFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign_up_details.view.*

class SignUpDetailsFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var user: User? = null

    private lateinit var userCollection: CollectionReference
    private lateinit var navController: NavController

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

        return inflater.inflate(R.layout.fragment_sign_up_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection(USERS_COLLECTION)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        view.mSkip.setOnClickListener {

            // Disable Interaction
            disableInteraction(requireActivity(), view.mIncludeProgressBar)

            // Set Default values
            user?.houseNumber = ""
            user?.blockNumber = 0
            user?.completeAddress = ""
            user?.gender = 0
            user?.contact = ""
            user?.fineCount = 0

            // Store user at Firestore
            Log.d(LOG_TAG, "saveUserAtFirestore (Skip): Started")
            saveUserAtFirestore(view, firebaseAuth.currentUser!!)

        }

        view.mDone.setOnClickListener {

            // Disable Interaction
            disableInteraction(requireActivity(), view.mIncludeProgressBar)

            // Validate fields
            val phoneNumber = view.mContactET.text.toString().trim()
            val houseNumber = view.mHouseNumET.text.toString().trim()

            if (phoneValidation(phoneNumber, view.mContactIL)) {
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
                return@setOnClickListener
            }
            if (emptyValidation(houseNumber, view.mHouseNumIL)) {
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
                return@setOnClickListener
            }

            if (view.mBlockSpinner.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG)
                    .show()
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
                return@setOnClickListener
            }

            // Set values
            user?.houseNumber = houseNumber
            user?.blockNumber = view.mBlockSpinner.selectedItemPosition
            user?.completeAddress = String.format(
                "House #%s, %s, Wapda Town, Lahore",
                houseNumber,
                view.mBlockSpinner.selectedItem.toString()
            )
            user?.gender = view.mGenderSpinner.selectedItemPosition
            user?.contact = phoneNumber
            user?.fineCount = 0

            // Start Registration Process
            Log.d(LOG_TAG, "saveUserAtFirestore (Done): Started")
            saveUserAtFirestore(view, firebaseAuth.currentUser!!)
        }
    }

    private fun saveUserAtFirestore(view: View, firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid)
            .set(user!!)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    sendVerificationEmail(view)

                } else {
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    Log.e(
                        LOG_TAG,
                        "saveUserAtFirestore -> addOnCompleteListener: task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
                Log.e(
                    LOG_TAG,
                    "saveUserAtFirestore -> addOnFailureListener: ${it.localizedMessage}",
                    it
                )
                return@addOnFailureListener
            }
    }

    private fun sendVerificationEmail(view: View) {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    Log.d(LOG_TAG, "Email Sent")
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    navController.navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
                }
            }?.addOnFailureListener { exception ->
                Log.e(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
            }
    }

}
