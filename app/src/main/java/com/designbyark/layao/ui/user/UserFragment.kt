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
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.common.formatGender
import com.designbyark.layao.common.isConnectedToInternet
import com.designbyark.layao.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_active_user.view.*
import java.util.*


class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.VISIBLE

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        if (firebaseUser == null) {
            val noUserView = inflater.inflate(
                R.layout.fragment_no_user,
                container,
                false
            )

            val registerButton: Button = noUserView.findViewById(R.id.mSignUp)
            val signInButton: Button = noUserView.findViewById(R.id.mSignIn)

            registerButton.setOnClickListener {
                navController.navigate(R.id.action_navigation_user_to_registerFragment)
            }

            signInButton.setOnClickListener {
                navController.navigate(R.id.action_navigation_user_to_signInFragment)
            }

            return noUserView
        }

        return inflater.inflate(R.layout.fragment_active_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUser == null) return

        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection(USERS_COLLECTION)

        getUserData(view, userCollection, firebaseUser!!)

        view.mEditButton.setOnClickListener {
            navController.navigate(R.id.action_navigation_user_to_editUserFragment)
        }

        view.sign_out_button.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            auth.signOut()
            navController.navigate(R.id.action_navigation_user_to_signInFragment)
        }
    }

    private fun getUserData(
        view: View,
        userCollection: CollectionReference,
        firebaseUser: FirebaseUser
    ) {
        userCollection.document(firebaseUser.uid)
            .addSnapshotListener { snapshot, e ->
                e?.printStackTrace()

                if (snapshot != null && snapshot.exists()) {
                    val model = snapshot.toObject(User::class.java)
                    if (model != null) {

                        val emptyString = "Not specified"

                        view.wallet_amount.text = String.format(
                            Locale.getDefault(),
                            "Rs. %.0f", model.wallet
                        )
                        view.full_name.text = model.fullName
                        view.email.text = model.email

                        if (model.completeAddress == "" || model.completeAddress.isEmpty()) {
                            view.address.text = emptyString
                        } else {
                            view.address.text = model.completeAddress
                        }

                        if (model.contact == "" || model.contact.isEmpty()) {
                            view.contact.text = emptyString
                        } else {
                            view.contact.text = model.contact
                        }
                        view.gender.text = formatGender(model.gender)
                    }
                }
            }
    }
}
