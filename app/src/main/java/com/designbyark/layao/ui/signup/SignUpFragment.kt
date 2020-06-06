package com.designbyark.layao.ui.signup


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentSignUpBinding
import com.designbyark.layao.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.*

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.signup = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomMenu.visibility = View.GONE
    }

    fun signUp() {
        if (!isConnectedToInternet(requireContext())) {
            Log.e(LOG_TAG, "Not connected to the internet!")
            return
        }

        disableInteraction(requireActivity(), binding.mIncludeProgressBar)
        startRegistrationProcess()
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
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            enableInteraction(requireActivity(), binding.mIncludeProgressBar)
            return
        } else {
            binding.mTermsText.setTextColor(
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        userModel.userId = user.uid
                        Log.d(LOG_TAG, "createUserWithEmailAndPassword: Started!")
                        updateAuthProfile(fullName, user, userModel)
                    } else {
                        Log.e(LOG_TAG, "currentUser: user is null")
                        enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                        return@addOnCompleteListener
                    }
                } else {
                    Log.e(LOG_TAG, "task: neither completed nor successful")
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
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
                    enableInteraction(
                        requireActivity(),
                        binding.mIncludeProgressBar
                    )
                    val action =
                        SignUpFragmentDirections.actionSignUpFragmentToSignUpDetailsFragment(
                            userModel
                        )
                    findNavController().navigate(action)
                } else {
                    enableInteraction(
                        requireActivity(),
                        binding.mIncludeProgressBar
                    )
                    Log.e(
                        LOG_TAG,
                        "updateAuthProfile -> updateProfile -> addOnCompleteListener: " +
                                "task is neither complete nor successful"
                    )
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                enableInteraction(
                    requireActivity(),
                    binding.mIncludeProgressBar
                )
                Log.d(
                    LOG_TAG,
                    "updateAuthProfile -> updateProfile -> addOnFailureListener: " +
                            it.localizedMessage,
                    it
                )
                return@addOnFailureListener
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}

