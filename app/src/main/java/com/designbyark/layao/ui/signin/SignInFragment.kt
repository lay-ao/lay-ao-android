package com.designbyark.layao.ui.signin


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.databinding.FragmentSignInBinding
import com.designbyark.layao.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        binding.signin = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.hide()
        }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomMenu.visibility = View.GONE

    }

    fun forgotPassword() {
        findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
    }

    fun signIn() {

        if (!isConnectedToInternet(requireContext())) {
            showNoInternetDialog()
            return
        }

        val email = binding.mEmailET.text.toString()
        if (emailValidation(email, binding.mEmailIL)) return

        val password = binding.mPasswordET.text.toString()
        if (passwordValidation(password, binding.mPasswordIL)) return

        disableInteraction(requireActivity(), binding.mIncludeProgressBar)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isComplete && task.isSuccessful) {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    findNavController().navigate(R.id.action_signInFragment_to_navigation_user)
                } else {
                    Log.e(
                        LOG_TAG,
                        "signInWithEmailAndPassword -> addOnCompleteListener: ${task.exception?.localizedMessage}"
                    )
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { exception ->

                if (exception is FirebaseAuthInvalidCredentialsException) {
                    notifyUser("Invalid Credentials")
                } else if (exception is FirebaseAuthInvalidUserException) {

                    when (exception.errorCode) {
                        "ERROR_USER_NOT_FOUND" -> showNoSignUpDialog()
                        "ERROR_USER_DISABLED" -> notifyUser("User is disabled, contact customer support (support@layao.com)")
                        else -> Log.d(LOG_TAG, exception.localizedMessage, exception)
                    }

                }

                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                return@addOnFailureListener
            }
    }

    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage("Kindly connect to a network to sign in")
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

    private fun showNoSignUpDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_home_black_24dp)
            .setTitle("No account found")
            .setMessage("No matching account found, either Sign Up or enter credentials again.")
            .setPositiveButton("Sign Up") { _, _ ->
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun notifyUser(error: String = "") {
        binding.errorMsg.visibility = View.VISIBLE
        binding.errorMsg.text = error
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    fun cancel() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.show()
        }
    }


}
