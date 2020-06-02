package com.designbyark.layao.ui.forgotpassword

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.util.disableInteraction
import com.designbyark.layao.util.emailValidation
import com.designbyark.layao.util.enableInteraction
import com.designbyark.layao.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentForgotPasswordBinding

    private val args: ForgotPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        binding.forgot = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val changePassword = getString(R.string.change_password)

        if (args.status == 0) {
            (requireActivity() as AppCompatActivity).run {
                supportActionBar?.setTitle(changePassword)
            }
            binding.mTitle.text = changePassword
            binding.mResetPassword.text = changePassword
        } else {
            binding.mTitle.text = getString(R.string.forgot_password)
        }
    }

    fun resetPassword() {
        val email = binding.mEmailET.text.toString()
        if (emailValidation(email, binding.mEmailIL)) return

        disableInteraction(
            requireActivity(),
            binding.mIncludeProgressBar
        )
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isComplete && task.isSuccessful) {
                    enableInteraction(
                        requireActivity(),
                        binding.mIncludeProgressBar
                    )
                    Log.d(LOG_TAG, "Password Reset Email Sent!")
                    Snackbar.make(binding.root, "Password Reset Email Sent", Snackbar.LENGTH_LONG)
                        .show()
                    findNavController().navigateUp()
                }
            }.addOnFailureListener { exception ->
                Log.e(LOG_TAG, exception.localizedMessage, exception)
                enableInteraction(
                    requireActivity(),
                    binding.mIncludeProgressBar
                )
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}
