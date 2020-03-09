package com.designbyark.layao.ui.user


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.common.duplicationValue
import com.designbyark.layao.common.emptyValidation
import com.designbyark.layao.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_edit_user.view.*

class EditUserFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var userDoc: DocumentReference

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val userCollection = firebaseFirestore.collection(USERS_COLLECTION)

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setTitle("Edit User")
//        }
//        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        if (firebaseUser != null) {
            userDoc = userCollection.document(firebaseUser.uid)
        } else {
            Toast.makeText(requireContext(), "No user found!", Toast.LENGTH_SHORT).show()
        }

        findUserData(view)

        view.mEditButton.setOnClickListener {
            validateUserData(view)
        }
    }

    private fun validateUserData(view: View) {

        val fullName = view.mFullNameET.text.toString().trim()
        if (emptyValidation(fullName, view.mFullNameIL)) return

        val houseNumber = view.mHouseNumET.text.toString().trim()
        if (emptyValidation(houseNumber, view.mHouseNumIL)) return

        val contact = view.mContactET.text.toString().trim()
        if (emptyValidation(contact, view.mContactIL)) return

        if (view.mBlockSpinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Kindly select block number", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Check for duplicate values
        if (duplicationValue(user!!.fullName, fullName)) {
            userDoc.update("fullName", fullName)
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(user!!.houseNumber, houseNumber)) {
            userDoc.update("houseNumber", houseNumber)
            Toast.makeText(requireContext(), "House Number updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(user!!.contact, contact)) {
            userDoc.update("contact", contact)
            Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
        }

        if (view.mBlockSpinner.selectedItemPosition != 0) {
            userDoc.update("blockNumber", view.mBlockSpinner.selectedItemPosition)
            userDoc.update(
                "completeAddress",
                "House #$houseNumber, ${view.mBlockSpinner.selectedItem}, Wapda Town, " +
                        "Lahore, Punjab, Pakistan"
            )
            Toast.makeText(requireContext(), "Block Number updated", Toast.LENGTH_SHORT).show()
        }


        if (view.mGenderSpinner.selectedItemPosition != user!!.gender) {
            userDoc.update("gender", view.mGenderSpinner.selectedItemPosition)
            Toast.makeText(requireContext(), "Gender updated", Toast.LENGTH_SHORT).show()
        }

        navController.navigate(R.id.action_editUserFragment_to_navigation_user)

    }

    private fun findUserData(view: View) {

        userDoc.addSnapshotListener { snapshot, e ->

            if (e != null) {
                Log.w(LOG_TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                user = snapshot.toObject(User::class.java)
                if (user != null) {
                    view.mFullNameET.setText(user!!.fullName, TextView.BufferType.EDITABLE)
                    view.mEmailET.setText(user!!.email, TextView.BufferType.EDITABLE)
                    view.mHouseNumET.setText(user!!.houseNumber, TextView.BufferType.EDITABLE)
                    view.mContactET.setText(user!!.contact, TextView.BufferType.EDITABLE)
                    view.mGenderSpinner.setSelection(user!!.gender)
                    if (user!!.blockNumber != 0) {
                        view.mBlockSpinner.setSelection(user!!.blockNumber)
                    }
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

}
