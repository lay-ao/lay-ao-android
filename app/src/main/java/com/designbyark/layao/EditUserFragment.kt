package com.designbyark.layao


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.common.duplicationValue
import com.designbyark.layao.common.emptyValidation
import com.designbyark.layao.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class EditUserFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var fullNameEditText: TextInputEditText

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText

    private lateinit var addressInputLayout: TextInputLayout
    private lateinit var addressEditText: TextInputEditText

    private lateinit var contactInputLayout: TextInputLayout
    private lateinit var contactEditText: TextInputEditText

    private lateinit var genderSpinner: Spinner

    private lateinit var deleteButton: Button
    private lateinit var editButton: Button

    private lateinit var userDoc: DocumentReference
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val userCollection = firebaseFirestore.collection(USERS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle("Edit User")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        if (firebaseUser != null) {
            userDoc = userCollection.document(firebaseUser.uid)
        } else {
            Toast.makeText(requireContext(), "No user found!", Toast.LENGTH_SHORT).show()
        }

        val root = inflater.inflate(R.layout.fragment_edit_user, container, false)

        findingViews(root)
        findUserData()

        editButton.setOnClickListener {
            validateUserData()
        }

        return root
    }

    private fun validateUserData() {

        val fullName = fullNameEditText.text.toString().trim()
        if (emptyValidation(fullName, fullNameInputLayout)) return

        val address = addressEditText.text.toString().trim()
        if (emptyValidation(address, addressInputLayout)) return

        val contact = contactEditText.text.toString().trim()
        if (emptyValidation(contact, contactInputLayout)) return

        // Check for duplicate values
        if (duplicationValue(user!!.fullName, fullName)) {
            userDoc.update("fullName", fullName)
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(user!!.completeAddress, address)) {
            userDoc.update("completeAddress", address)
            Toast.makeText(requireContext(), "Address updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(user!!.contact, contact)) {
            userDoc.update("contact", contact)
            Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
        }

        if (genderSpinner.selectedItemPosition != user!!.gender) {
            userDoc.update("gender", genderSpinner.selectedItemPosition)
            Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
        }

        navController.navigate(R.id.action_editUserFragment_to_navigation_user)

    }

    private fun findUserData() {

        userDoc.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(LOG_TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                user = snapshot.toObject(User::class.java)
                if (user != null) {
                    fullNameEditText.setText(user!!.fullName, TextView.BufferType.EDITABLE)
                    emailEditText.setText(user!!.email, TextView.BufferType.EDITABLE)
                    addressEditText.setText(user!!.completeAddress, TextView.BufferType.EDITABLE)
                    contactEditText.setText(user!!.contact, TextView.BufferType.EDITABLE)
                    genderSpinner.setSelection(user!!.gender)
                }
            }
        }

    }

    private fun findingViews(root: View) {
        root.run {
            fullNameInputLayout = findViewById(R.id.full_name_input_layout)
            fullNameEditText = findViewById(R.id.full_name_edit_text)
            emailInputLayout = findViewById(R.id.email_input_layout)
            emailEditText = findViewById(R.id.email_input_edit_text)
            addressInputLayout = findViewById(R.id.address_input_layout)
            addressEditText = findViewById(R.id.address_edit_text)
            contactInputLayout = findViewById(R.id.contact_input_layout)
            contactEditText = findViewById(R.id.contact_edit_text)
            genderSpinner = findViewById(R.id.gender_spinner)
            deleteButton = findViewById(R.id.delete_button)
            editButton = findViewById(R.id.edit_button)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

}
