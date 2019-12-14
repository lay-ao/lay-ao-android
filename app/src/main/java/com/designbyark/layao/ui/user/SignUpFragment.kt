package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    companion object {
        const val GALLERY_INTENT = 101
        const val IMAGE_SIZE = 2_000_000;
    }

    private lateinit var navController: NavController

    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout

    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText

    private lateinit var termsAndCondition: CheckBox
    private lateinit var registerButton: Button
    private lateinit var termsAndConditionView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection(USERS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val root = inflater.inflate(R.layout.fragment_sign_up, container, false)

        findingViews(root)

        registerButton.setOnClickListener {
            startRegistrationProcess(auth, userCollection)
        }

        return root
    }

    private fun startRegistrationProcess(
        auth: FirebaseAuth,
        userCollection: CollectionReference
    ) {

        if (!termsAndCondition.isChecked) {
            Toast.makeText(
                requireContext(),
                "Kindly, accept our Terms & Condition before proceeding", Toast.LENGTH_SHORT
            ).show()
            termsAndConditionView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            return
        } else {
            termsAndConditionView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_dark
                )
            )
        }

        val fullName = fullNameEditText.text.toString()
        if (emptyValidation(fullName, fullNameInputLayout)) return

        val email = emailEditText.text.toString()
        if (emailValidation(email, emailInputLayout)) return

        val password = passwordEditText.text.toString()
        if (passwordValidation(password, passwordInputLayout)) return

        val confirmPassword = confirmPasswordEditText.text.toString()
        if (confirmPasswordValidation(confirmPassword, password, confirmPasswordInputLayout)) return

        val userModel = User()
        userModel.fullName = fullName
        userModel.email = email
        userModel.password = confirmPassword
        userModel.wallet = 0.0
        userModel.completeAddress = ""
        userModel.houseNumber = ""
        userModel.blockNumber = ""
        userModel.contact = ""
        userModel.gender = 0
        userModel.favoriteItems = emptyList()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    userModel.userId = user.uid
                    updateAuthProfile(fullName, user, userCollection, userModel)
                }
            }
        }
    }

    private fun findingViews(root: View) {
        root.run {
            fullNameInputLayout = findViewById(R.id.full_name_input_layout)
            emailInputLayout = findViewById(R.id.email_input_layout)
            passwordInputLayout = findViewById(R.id.password_input_layout)
            confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout)
            confirmPasswordEditText = findViewById(R.id.confirm_password_input_edit_text)
            passwordEditText = findViewById(R.id.password_input_edit_text)
            emailEditText = findViewById(R.id.email_input_edit_text)
            fullNameEditText = findViewById(R.id.full_name_edit_text)
            termsAndCondition = findViewById(R.id.terms_and_condition)
            registerButton = findViewById(R.id.register_button)
            termsAndConditionView = findViewById(R.id.terms_and_condition_view)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun updateAuthProfile(
        fullName: String,
        user: FirebaseUser,
        userCollection: CollectionReference,
        userModel: User
    ) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful && updateTask.isComplete) {
                    saveUserToFirestore(userCollection, user, userModel)
                }
            }
            .addOnFailureListener {
                Log.d(
                    LOG_TAG,
                    it.localizedMessage,
                    it
                )
            }
    }

    private fun saveUserToFirestore(
        userCollection: CollectionReference,
        user: FirebaseUser,
        userModel: User
    ) {
        userCollection.document(user.uid)
            .set(userModel)
            .addOnCompleteListener { sTask ->
                if (sTask.isComplete && sTask.isSuccessful) {
                    navController.navigate(R.id.action_registerFragment_to_navigation_user)
                } else {
                    Log.d(
                        LOG_TAG,
                        "Saving user data failed"
                    )
                }
            }
            .addOnFailureListener {
                Log.d(
                    LOG_TAG,
                    it.localizedMessage,
                    it
                )
            }
    }

}

