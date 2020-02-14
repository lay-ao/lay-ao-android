package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpDetailsFragment : Fragment() {

    private lateinit var houseNumberInput: TextInputLayout
    private lateinit var houseNumberEditText: EditText

    private lateinit var contactInput: TextInputLayout
    private lateinit var contactEditText: EditText

    private lateinit var blockSpinner: Spinner
    private lateinit var genderSpinner: Spinner

    private lateinit var skipButton: Button
    private lateinit var doneButton: Button

    private var user: User? = null

    private lateinit var userCollection: CollectionReference
    private lateinit var navController: NavController

    private lateinit var progressBarLayout: View

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

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection(USERS_COLLECTION)

        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment
        )

        val view = inflater.inflate(R.layout.fragment_sign_up_details, container, false)

        setupViews(view)

        skipButton.setOnClickListener {

            // Disable Interaction
            disableInteraction(requireActivity(), progressBarLayout)

            // Set Default values
            user?.houseNumber = ""
            user?.blockNumber = 0
            user?.completeAddress = ""
            user?.gender = 0
            user?.contact = ""

            // Store user at Firestore
            Log.d(LOG_TAG, "saveUserAtFirestore (Skip): Started")
            saveUserAtFirestore(firebaseAuth.currentUser!!)

        }

        doneButton.setOnClickListener {

            // Disable Interaction
            disableInteraction(requireActivity(), progressBarLayout)

            // Validate fields
            val phoneNumber = contactEditText.text.toString().trim()
            val houseNumber = houseNumberEditText.text.toString().trim()

            if (phoneValidation(phoneNumber, contactInput)) {
                enableInteraction(requireActivity(), progressBarLayout)
                return@setOnClickListener
            }
            if (emptyValidation(houseNumber, houseNumberInput)) {
                enableInteraction(requireActivity(), progressBarLayout)
                return@setOnClickListener
            }

            if (blockSpinner.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG)
                    .show()
                enableInteraction(requireActivity(), progressBarLayout)
                return@setOnClickListener
            }

            // Set values
            user?.houseNumber = houseNumber
            user?.blockNumber = blockSpinner.selectedItemPosition
            user?.completeAddress = String.format(
                "House #%s, %s, Wapda Town, Lahore",
                houseNumber,
                blockSpinner.selectedItem.toString()
            )
            user?.gender = genderSpinner.selectedItemPosition
            user?.contact = phoneNumber

            // Start Registration Process
            Log.d(LOG_TAG, "saveUserAtFirestore (Done): Started")
            saveUserAtFirestore(firebaseAuth.currentUser!!)
        }

        return view
    }

    private fun setupViews(view: View) {
        view.apply {
            houseNumberInput = findViewById(R.id.house_no_input_layout)
            houseNumberEditText = findViewById(R.id.house_no_edit_text)
            contactInput = findViewById(R.id.contact_input_layout)
            contactEditText = findViewById(R.id.contact_edit_text)
            blockSpinner = findViewById(R.id.block_spinner)
            genderSpinner = findViewById(R.id.gender_spinner)
            skipButton = findViewById(R.id.skip_button)
            doneButton = findViewById(R.id.done_button)
            progressBarLayout = findViewById(R.id.include_progress_bar)
        }
    }

    private fun saveUserAtFirestore(firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid)
            .set(user!!)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    enableInteraction(requireActivity(), progressBarLayout)
                    navController.navigate(R.id.action_signUpDetailsFragment_to_navigation_user)
                } else {
                    enableInteraction(requireActivity(), progressBarLayout)
                    Log.e(LOG_TAG, "saveUserAtFirestore -> addOnCompleteListener: task is neither complete nor successful")
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(requireActivity(), progressBarLayout)
                Log.e(LOG_TAG, "saveUserAtFirestore -> addOnFailureListener: ${it.localizedMessage}", it)
                return@addOnFailureListener
            }
    }

}
