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
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    companion object {
        const val KEY = "USER_KEY"
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
    private lateinit var progressBarLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val auth = FirebaseAuth.getInstance()

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

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            disableInteraction(requireActivity(), progressBarLayout)

            startRegistrationProcess(auth)
        }

        return root
    }

    private fun startRegistrationProcess(
        auth: FirebaseAuth
    ) {

        val fullName = fullNameEditText.text.toString()
        if (emptyValidation(fullName, fullNameInputLayout)) {
            enableInteraction(requireActivity(), progressBarLayout)
            return
        }

        val email = emailEditText.text.toString()
        if (emailValidation(email, emailInputLayout)) {
            enableInteraction(requireActivity(), progressBarLayout)
            return
        }

        val password = passwordEditText.text.toString()
        if (passwordValidation(password, passwordInputLayout)) {
            enableInteraction(requireActivity(), progressBarLayout)
            return
        }

        val confirmPassword = confirmPasswordEditText.text.toString()
        if (confirmPasswordValidation(confirmPassword, password, confirmPasswordInputLayout)) {
            enableInteraction(requireActivity(), progressBarLayout)
            return
        }

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
            enableInteraction(requireActivity(), progressBarLayout)
            return
        } else {
            termsAndConditionView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_dark
                )
            )
        }

        val userModel = User()
        userModel.fullName = fullName
        userModel.email = email
        userModel.password = confirmPassword
        userModel.wallet = 0.0

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    userModel.userId = user.uid
                    Log.d(LOG_TAG, "createUserWithEmailAndPassword: Started!")
                    updateAuthProfile(fullName, user, userModel)
                } else {
                    Log.e(
                        LOG_TAG,
                        "createUserWithEmailAndPassword -> addOnCompleteListener -> currentUser: user is null"
                    )
                    enableInteraction(requireActivity(), progressBarLayout)
                    return@addOnCompleteListener
                }
            }
        }
    }

    private fun updateAuthProfile(fullName: String, user: FirebaseUser, userModel: User) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.isComplete) {

                    Log.d(LOG_TAG, "updateProfile: Started!")
                    val args = Bundle()
                    args.putSerializable(KEY, userModel)
                    enableInteraction(requireActivity(), progressBarLayout)
                    navController.navigate(
                        R.id.action_signUpFragment_to_signUpDetailsFragment,
                        args
                    )
                } else {
                    enableInteraction(requireActivity(), progressBarLayout)
                    Log.e(
                        LOG_TAG,
                        "updateAuthProfile -> updateProfile -> addOnCompleteListener: " +
                                "task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(requireActivity(), progressBarLayout)
                Log.d(
                    LOG_TAG,
                    "updateAuthProfile -> updateProfile -> addOnFailureListener: " +
                            it.localizedMessage,
                    it
                )
                return@addOnFailureListener
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
            progressBarLayout = findViewById(R.id.include_progress_bar)
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

}

