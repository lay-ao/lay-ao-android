package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.common.formatGender
import com.designbyark.layao.common.isConnectedToInternet
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentActiveUserBinding
import com.designbyark.layao.databinding.FragmentNoUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    private lateinit var emptyBinding: FragmentNoUserBinding
    private lateinit var binding: FragmentActiveUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility != View.VISIBLE) {
            bottomMenu.visibility = View.VISIBLE
        }

        if (firebaseUser == null) {
            emptyBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_no_user, container, false)
            emptyBinding.user = this
            return emptyBinding.root
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_active_user, container, false)
        binding.user = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUser == null) {
            (requireActivity() as AppCompatActivity).run {
                supportActionBar?.setTitle(R.string.title_user)
            }
            return
        }
        setHasOptionsMenu(true)

        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection(USERS_COLLECTION)

        getUserData(view, userCollection, firebaseUser!!)
    }

    private fun getUserData(
        view: View,
        userCollection: CollectionReference,
        firebaseUser: FirebaseUser
    ) {
        userCollection.document(firebaseUser.uid)
            .addSnapshotListener { snapshot, e ->

                if (snapshot != null && snapshot.exists()) {
                    val model = snapshot.toObject(User::class.java)
                    if (model != null) {

                        val emptyString = "Not specified"

                        binding.mFullName.text = model.fullName
                        binding.mEmail.text = model.email

                        if (model.completeAddress == "" || model.completeAddress.isEmpty()) {
                            binding.mAddress.text = emptyString
                        } else {
                            binding.mAddress.text = model.completeAddress
                        }

                        if (model.contact == "" || model.contact.isEmpty()) {
                            binding.mContact.text = emptyString
                        } else {
                            binding.mContact.text = model.contact
                        }
                        binding.mGender.text = formatGender(model.gender)
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (firebaseUser != null) {
            menu.clear()
            inflater.inflate(R.menu.user_menu, menu)
        } else {
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    fun startRegistration() {
        findNavController().navigate(R.id.action_navigation_user_to_registerFragment)
    }

    fun login() {
        findNavController().navigate(R.id.action_navigation_user_to_signInFragment)
    }

    fun signOut() {
        if (!isConnectedToInternet(requireContext())) {
            Log.e(LOG_TAG, "Not connected to the internet!")
            return
        }

        auth.signOut()
        findNavController().navigate(R.id.action_navigation_user_to_signInFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.user_edit -> {
                findNavController().navigate(R.id.action_navigation_user_to_editUserFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
