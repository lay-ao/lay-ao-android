package com.designbyark.layao.ui.user


import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.emailValidation
import com.designbyark.layao.common.passwordValidation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    private lateinit var signInButton: Button
    private lateinit var forgotPassword: TextView

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

        val root = inflater.inflate(R.layout.fragment_sign_in, container, false)

        findingViews(root)

        signInButton.setOnClickListener {

            val email = emailEditText.text.toString()
            if (emailValidation(email, emailInputLayout)) return@setOnClickListener

            val password = passwordEditText.text.toString()
            if (passwordValidation(password, passwordInputLayout)) return@setOnClickListener

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isComplete && task.isSuccessful) {
                        // TODO SAVE USER DATA TO LOCAL DB
                        navController.navigate(R.id.action_signInFragment_to_navigation_user)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }

        return root
    }

    private fun findingViews(root: View) {
        root.run {
            emailInputLayout = findViewById(R.id.email_input_layout)
            passwordInputLayout = findViewById(R.id.password_input_layout)
            emailEditText = findViewById(R.id.email_input_edit_text)
            passwordEditText = findViewById(R.id.password_input_edit_text)
            signInButton = findViewById(R.id.sign_in_button)
            forgotPassword = findViewById(R.id.forgot_password)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }


}
