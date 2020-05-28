package com.designbyark.layao.ui.signup


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignUpFragment : Fragment() {

    companion object {
        const val KEY = "USER_KEY"
    }

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }
//        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomMenu.visibility = View.GONE

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        view.mSignUp.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            disableInteraction(requireActivity(), view.mIncludeProgressBar)

            startRegistrationProcess(view, auth)
        }
    }

    private fun startRegistrationProcess(view: View, auth: FirebaseAuth) {

        val fullName = view.mFullNameET.text.toString()
        if (emptyValidation(fullName, view.mFullNameIL)) {
            enableInteraction(requireActivity(), view.mIncludeProgressBar)
            return
        }

        val email = view.mEmailET.text.toString()
        if (emailValidation(email, view.mEmailIL)) {
            enableInteraction(requireActivity(), view.mIncludeProgressBar)
            return
        }

        val password = view.mPasswordET.text.toString()
        if (passwordValidation(password, view.mPasswordIL)) {
            enableInteraction(requireActivity(), view.mIncludeProgressBar)
            return
        }

        val confirmPassword = view.mConfirmPassET.text.toString()
        if (confirmPasswordValidation(
                confirmPassword,
                password,
                view.mConfirmPassIL
            )
        ) {
            enableInteraction(requireActivity(), view.mIncludeProgressBar)
            return
        }

        if (!view.mTerms.isChecked) {
            Toast.makeText(
                requireContext(),
                "Kindly, accept our Terms & Condition before proceeding", Toast.LENGTH_SHORT
            ).show()
            view.mTermsText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            enableInteraction(requireActivity(), view.mIncludeProgressBar)
            return
        } else {
            view.mTermsText.setTextColor(
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
                    updateAuthProfile(view, fullName, user, userModel)
                } else {
                    Log.e(
                        LOG_TAG,
                        "createUserWithEmailAndPassword -> addOnCompleteListener -> currentUser: user is null"
                    )
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    return@addOnCompleteListener
                }
            }
        }
    }

    private fun updateAuthProfile(view: View, fullName: String, user: FirebaseUser, userModel: User) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.isComplete) {

                    Log.d(LOG_TAG, "updateProfile: Started!")
                    val args = Bundle()
                    args.putSerializable(KEY, userModel)
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    navController.navigate(
                        R.id.action_signUpFragment_to_signUpDetailsFragment,
                        args
                    )
                } else {
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    Log.e(
                        LOG_TAG,
                        "updateAuthProfile -> updateProfile -> addOnCompleteListener: " +
                                "task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(requireActivity(), view.mIncludeProgressBar)
                Log.d(
                    LOG_TAG,
                    "updateAuthProfile -> updateProfile -> addOnFailureListener: " +
                            it.localizedMessage,
                    it
                )
                return@addOnFailureListener
            }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> navController.navigateUp()
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        menu.clear()
//    }

}

