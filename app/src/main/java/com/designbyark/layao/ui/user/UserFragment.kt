package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class UserFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.VISIBLE

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        if (firebaseUser != null) {
            Log.d(LOG_TAG, "User is not null!")
        } else {
            val noUserView = inflater.inflate(
                R.layout.fragment_no_user,
                container,
                false
            )

            val registerButton: Button = noUserView.findViewById(R.id.register_button)
            val signInButton: Button = noUserView.findViewById(R.id.sign_in_button)

            registerButton.setOnClickListener {
                navController.navigate(R.id.action_navigation_user_to_registerFragment)
            }

            signInButton.setOnClickListener {
                navController.navigate(R.id.action_navigation_user_to_signInFragment)
            }

            return noUserView
        }

        val root = inflater.inflate(R.layout.fragment_active_user, container, false)

        val signOut: Button = root.findViewById(R.id.sign_out_button)

        signOut.setOnClickListener {
            auth.signOut()
        }

        return root
    }


}
