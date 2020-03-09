package com.designbyark.layao.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.disableInteraction
import com.designbyark.layao.common.emailValidation
import com.designbyark.layao.common.enableInteraction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        view.mResetPassword.setOnClickListener {

            val email = view.mEmailET.text.toString()
            if (emailValidation(email, view.mEmailIL)) return@setOnClickListener

            disableInteraction(requireActivity(), view.mIncludeProgressBar)
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isComplete && task.isSuccessful) {
                        enableInteraction(requireActivity(), view.mIncludeProgressBar)
                        Log.d(LOG_TAG, "Password Reset Email Sent!")
                        Snackbar.make(view, "Password Reset Email Sent", Snackbar.LENGTH_LONG)
                            .show()
                        navController.navigateUp()
                    }
                }.addOnFailureListener { exception ->
                    Log.e(LOG_TAG, exception.localizedMessage, exception)
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                }

        }

    }


}
