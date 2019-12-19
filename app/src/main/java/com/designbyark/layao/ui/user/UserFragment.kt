package com.designbyark.layao.ui.user


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.common.formatGender
import com.designbyark.layao.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class UserFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var walletView: TextView
    private lateinit var fullNameView: TextView
    private lateinit var emailView: TextView
    private lateinit var addressView: TextView
    private lateinit var contactView: TextView
    private lateinit var genderView: TextView
    private lateinit var favoriteItemsLabel: TextView

    private lateinit var favoriteItems: RecyclerView

    private lateinit var signOut: Button

    private lateinit var edit: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection(USERS_COLLECTION)

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

        findingViews(root)

        getUserData(userCollection, firebaseUser)

        edit.setOnClickListener {
            navController.navigate(R.id.action_navigation_user_to_editUserFragment)
        }

        signOut.setOnClickListener {
            auth.signOut()
            navController.navigate(R.id.action_navigation_user_to_signInFragment)
        }

        return root
    }

    private fun getUserData(
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

                        walletView.text = String.format(
                            Locale.getDefault(),
                            "Rs. %.0f", model.wallet
                        )
                        fullNameView.text = model.fullName
                        emailView.text = model.email

                        if (model.completeAddress == "" || model.completeAddress.isEmpty()) {
                            addressView.text = emptyString
                        } else {
                            addressView.text = model.completeAddress
                        }

                        if (model.contact == "" || model.contact.isEmpty()) {
                            contactView.text = emptyString
                        } else {
                            contactView.text = model.contact
                        }
                        genderView.text = formatGender(model.gender)

                        if (model.favoriteItems.isEmpty()) {
                            favoriteItems.visibility = View.GONE
                            favoriteItemsLabel.text = "No Favorite Items Yet!"
                            favoriteItemsLabel.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    android.R.color.holo_purple
                                )
                            )
                        }

                    }
                }
            }
    }

    private fun findingViews(root: View) {
        root.run {
            walletView = findViewById(R.id.wallet_amount)
            fullNameView = findViewById(R.id.full_name)
            emailView = findViewById(R.id.email)
            addressView = findViewById(R.id.address)
            contactView = findViewById(R.id.contact)
            genderView = findViewById(R.id.gender)
            favoriteItems = findViewById(R.id.favorite_items_recycler_view)
            signOut = findViewById(R.id.sign_out_button)
            edit = findViewById(R.id.edit_button)
            favoriteItemsLabel = findViewById(R.id.favorite_items_label)
        }
    }


}
