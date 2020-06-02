package com.designbyark.layao.ui.edituser


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.util.USERS_COLLECTION
import com.designbyark.layao.util.duplicationValue
import com.designbyark.layao.util.emptyValidation
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentEditUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class EditUserFragment : Fragment() {

    private lateinit var userDoc: DocumentReference
    private var user: User? = null
    private lateinit var binding: FragmentEditUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_user, container, false)
        binding.edit = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val userCollection = firebaseFirestore.collection(USERS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle("Edit User")
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        if (firebaseUser != null) {
            userDoc = userCollection.document(firebaseUser.uid)
        } else {
            Toast.makeText(requireContext(), "No user found!", Toast.LENGTH_SHORT).show()
        }

        findUserData()
    }

    fun editUser() {

        val fullName = binding.mFullNameET.text.toString().trim()
        if (emptyValidation(
                fullName,
                binding.mFullNameIL
            )
        ) return

        val houseNumber = binding.mHouseNumET.text.toString().trim()
        if (emptyValidation(
                houseNumber,
                binding.mHouseNumIL
            )
        ) return

        val contact = binding.mContactET.text.toString().trim()
        if (emptyValidation(
                contact,
                binding.mContactIL
            )
        ) return

        if (binding.mBlockSpinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Kindly select block number", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Check for duplicate values
        if (duplicationValue(
                user!!.fullName,
                fullName
            )
        ) {
            userDoc.update("fullName", fullName)
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(
                user!!.houseNumber,
                houseNumber
            )
        ) {
            userDoc.update("houseNumber", houseNumber)
            Toast.makeText(requireContext(), "House Number updated", Toast.LENGTH_SHORT).show()
        }

        if (duplicationValue(user!!.contact, contact)) {
            userDoc.update("contact", contact)
            Toast.makeText(requireContext(), "Contact updated", Toast.LENGTH_SHORT).show()
        }

        if (binding.mBlockSpinner.selectedItemPosition != 0) {
            userDoc.update("blockNumber", binding.mBlockSpinner.selectedItemPosition)
            userDoc.update(
                "completeAddress",
                "House #$houseNumber, ${binding.mBlockSpinner.selectedItem}, Wapda Town, " +
                        "Lahore, Punjab, Pakistan"
            )
            Toast.makeText(requireContext(), "Block Number updated", Toast.LENGTH_SHORT).show()
        }

        if (binding.mGenderSpinner.selectedItemPosition != user!!.gender) {
            userDoc.update("gender", binding.mGenderSpinner.selectedItemPosition)
            Toast.makeText(requireContext(), "Gender updated", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigate(R.id.action_editUserFragment_to_navigation_user)

    }

    // TODO: Start from here
    fun changePassword() {
        val action = EditUserFragmentDirections.actionEditUserFragmentToForgotPasswordFragment(0)
        findNavController().navigate(action)
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
                    binding.mFullNameET.setText(user!!.fullName, TextView.BufferType.EDITABLE)
                    binding.mEmailET.setText(user!!.email, TextView.BufferType.EDITABLE)
                    binding.mHouseNumET.setText(user!!.houseNumber, TextView.BufferType.EDITABLE)
                    binding.mContactET.setText(user!!.contact, TextView.BufferType.EDITABLE)
                    binding.mGenderSpinner.setSelection(user!!.gender)
                    if (user!!.blockNumber != 0) {
                        binding.mBlockSpinner.setSelection(user!!.blockNumber)
                    }
                    binding.mAddressHelp.text =
                        String.format("House #%s, %s, Wapda Town, Lahore",
                            user!!.houseNumber,
                            binding.mBlockSpinner.selectedItem
                        )
                }
            }
        }

    }

}
