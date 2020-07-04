package com.designbyark.layao.ui.user


import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentActiveUserBinding
import com.designbyark.layao.databinding.FragmentNoUserBinding
import com.designbyark.layao.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class UserFragment : Fragment() {

    private lateinit var userCollection: CollectionReference
    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser? = null

    private lateinit var emptyBinding: FragmentNoUserBinding
    private lateinit var binding: FragmentActiveUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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

        val title = auth.currentUser?.displayName ?: "You"
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.title = title
        }

        if (firebaseUser == null) {
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        userCollection = firestore.collection(USERS_COLLECTION)

        if (isConnectedToInternet(view.context)) {
            getUserData(userCollection, firebaseUser!!)
        } else {
            val textViews = listOf(
                binding.mFullName, binding.mEmail,
                binding.mAddress, binding.mContact, binding.mGender
            )
            for (textView in textViews) {
                setLoadingView(textView)
            }
        }
        getToken()
    }

    private fun setLoadingView(view: TextView) {
        view.text = getString(R.string.loading)
        view.setTextColor(ContextCompat.getColor(view.context, android.R.color.darker_gray))
        view.setTypeface(null, Typeface.ITALIC)
    }

    private fun setDataView(view: TextView, value: String) {
        if (value == "" || value.isEmpty() || value.isBlank()) {
            view.text = "N/A"
            view.setTextColor(ContextCompat.getColor(view.context, android.R.color.darker_gray))
        } else {
            view.text = value
            view.setTextColor(ContextCompat.getColor(view.context, android.R.color.black))
        }
        view.setTypeface(null, Typeface.NORMAL)
    }

    private fun getUserData(userCollection: CollectionReference, firebaseUser: FirebaseUser) {
        userCollection.document(firebaseUser.uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(User::class.java)
                if (model != null) {
                    setDataView(binding.mFullName, model.fullName)
                    setDataView(binding.mEmail, model.email)
                    setDataView(binding.mAddress, model.completeAddress)
                    setDataView(binding.mContact, model.contact)
                    setDataView(binding.mGender, formatGender(model.gender))
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {


        if (firebaseUser != null) {
            requireActivity().invalidateOptionsMenu()
            if (!isConnectedToInternet(requireContext())) {
                menu.findItem(R.id.no_wifi).isVisible = true
                menu.findItem(R.id.user_edit).isVisible = false
            } else {
                getUserData(userCollection, firebaseUser!!)
            }
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (firebaseUser != null) {
            menu.clear()
            inflater.inflate(R.menu.user_menu, menu)
        } else {
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage(
                "Search requires network connection, kindly connect to a " +
                        "network and try searching again"
            )
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
            R.id.no_wifi -> {
                showNoInternetDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
