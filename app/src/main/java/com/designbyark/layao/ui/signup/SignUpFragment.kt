package com.designbyark.layao.ui.signup


import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.MainActivity
import com.designbyark.layao.R
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentSignUpBinding
import com.designbyark.layao.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userCollection: CollectionReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.signup = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection("Users")

        val spannable = SpannableString(getString(R.string.terms_and_condition_label))
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            15, 35,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.mTermsText.text = spannable

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.hide()
        }

        (requireActivity() as MainActivity).binding.bottomNavView.visibility = View.GONE
    }

    fun signUp() {
        if (!isConnectedToInternet(requireContext())) {
            showNoInternetDialog()
            return
        }

        disableInteraction(requireActivity(), binding.mIncludeProgressBar)
        startRegistrationProcess()
    }

    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage("Kindly connect to a network to sign up")
            .setPositiveButton("Try Again") { dialog, _ ->
                if (isConnectedToInternet(requireContext())) {
                    dialog.dismiss()
                } else {
                    showNoInternetDialog()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startRegistrationProcess() {

        val fullName = binding.mFullNameET.text.toString()
        if (emptyValidation(fullName, binding.mFullNameIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        val email = binding.mEmailET.text.toString()
        if (emailValidation(email, binding.mEmailIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        val password = binding.mPasswordET.text.toString()
        if (passwordValidation(password, binding.mPasswordIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        val confirmPassword = binding.mConfirmPassET.text.toString()
        if (confirmPasswordValidation(confirmPassword, password, binding.mConfirmPassIL)) {
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        }

        if (!binding.mTerms.isChecked) {
            binding.mTermsText.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            )
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        } else {
            binding.mTermsText.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)
            )
        }

        val userModel = User()
        userModel.fullName = fullName
        userModel.email = email
        userModel.password = confirmPassword
        userModel.houseNumber = ""
        userModel.blockNumber = 0
        userModel.completeAddress = ""
        userModel.gender = 0
        userModel.contact = ""
        userModel.token = ""

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        userModel.userId = user.uid
                        Log.d(LOG_TAG, "createUserWithEmailAndPassword: Started!")
                        updateAuthProfile(fullName, user, userModel)
                    } else {
                        Log.e(LOG_TAG, "Current user is null")
                        enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                        return@addOnCompleteListener
                    }
                } else {
                    Log.e(LOG_TAG, "Cannot create user with email and password")
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthUserCollisionException) {
                    notifyUser(getString(R.string.user_collision_msg))
                } else {
                    binding.errorMsg.visibility = View.GONE
                    binding.mPasswordIL.error = null
                    binding.mConfirmPassIL.error = null
                }
            }
    }

    private fun notifyUser(error: String) {
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = error
    }

    private fun updateAuthProfile(fullName: String, user: FirebaseUser, userModel: User) {

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.isComplete) {
                    Log.d(LOG_TAG, "updateProfile: Started!")
                    saveUserAtFirestore(user, userModel)
                } else {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    Log.e(
                        LOG_TAG,
                        "updateAuthProfile -> updateProfile -> addOnCompleteListener: " +
                                "task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                return@addOnFailureListener
            }
    }

    private fun saveUserAtFirestore(user: FirebaseUser, userModel: User) {

        userCollection.document(user.uid).set(userModel).addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
                sendVerificationEmail(user, userModel)
            } else {
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                Log.d(LOG_TAG, "Unable to save user data, reason:", task.exception)
            }
        }.addOnFailureListener { exception ->
            Log.d(LOG_TAG, exception.localizedMessage, exception)
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return@addOnFailureListener
        }
    }

    private fun sendVerificationEmail(user: FirebaseUser, userModel: User) {

        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isComplete && task.isSuccessful) {
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                val action =
                    SignUpFragmentDirections.actionSignUpFragmentToSignUpDetailsFragment(userModel)
                findNavController().navigate(action)
            } else {
                Log.d(LOG_TAG, "Unable to send verification email, reason:", task.exception)
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                return@addOnCompleteListener
            }
        }.addOnFailureListener { exception ->
            Log.d(LOG_TAG, exception.localizedMessage, exception)
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return@addOnFailureListener
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    fun cancel() {
        findNavController().navigateUp()
    }

    fun termsAndConditions() {
        findNavController().navigate(R.id.action_signUpFragment_to_TACFragment)
    }

}

