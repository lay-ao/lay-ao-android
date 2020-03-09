package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class SignInFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
//
//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        }
//        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        view.mSignIn.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            val email = view.mEmailET.text.toString()
            if (emailValidation(email, view.mEmailIL)) return@setOnClickListener

            val password = view.mPasswordET.text.toString()
            if (passwordValidation(password, view.mPasswordIL)) return@setOnClickListener

            disableInteraction(requireActivity(), view.mIncludeProgressBar)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isComplete && task.isSuccessful) {
                        enableInteraction(requireActivity(), view.mIncludeProgressBar)
                        navController.navigate(R.id.action_signInFragment_to_navigation_user)
                    } else {
                        Log.e(
                            LOG_TAG,
                            "signInWithEmailAndPassword -> addOnCompleteListener: ${task.exception?.localizedMessage}"
                        )
                        enableInteraction(requireActivity(), view.mIncludeProgressBar)
                        return@addOnCompleteListener
                    }
                }
                .addOnFailureListener {
                    Log.e(
                        LOG_TAG,
                        "signInWithEmailAndPassword -> addOnFailureListener: ${it.localizedMessage}"
                    )
                    enableInteraction(requireActivity(), view.mIncludeProgressBar)
                    return@addOnFailureListener
                }
        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> navController.navigateUp()
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        menu.clear()
//    }


}
