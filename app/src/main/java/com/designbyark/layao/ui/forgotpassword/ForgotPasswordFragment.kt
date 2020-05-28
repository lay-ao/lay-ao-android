package com.designbyark.layao.ui.forgotpassword

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
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

    private var title: Int? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            title = it?.getInt("change_pass")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val changePassword = view.context.resources.getString(R.string.change_password)

        if (title == 0) {
            (requireActivity() as AppCompatActivity).run {
                supportActionBar?.setTitle(changePassword)
            }
            view.mTitle.text = changePassword
            view.mResetPassword.text = changePassword
        } else {
            view.mTitle.text = view.context.resources.getString(R.string.forgot_password)
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

}
