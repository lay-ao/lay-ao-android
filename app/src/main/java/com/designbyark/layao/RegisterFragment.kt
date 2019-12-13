package com.designbyark.layao


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.designbyark.layao.common.*
import com.designbyark.layao.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    companion object {
        const val GALLERY_INTENT = 101
        const val IMAGE_SIZE = 2_000_000;
    }

    private lateinit var navController: NavController

    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout

    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText

    private lateinit var imageView: ImageView
    private lateinit var termsAndCondition: CheckBox
    private lateinit var registerButton: Button
    private lateinit var termsAndConditionView: TextView
    private lateinit var profilePicInstruction: TextView

    private var isImageChanged: Boolean = false
    private var selectedImage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("Users")

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        val root = inflater.inflate(R.layout.fragment_register, container, false)

        findingViews(root)

        imageView.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, GALLERY_INTENT)
        }

        registerButton.setOnClickListener {
            startRegistrationProcess(auth, userCollection)
        }

        return root
    }

    private fun startRegistrationProcess(
        auth: FirebaseAuth,
        userCollection: CollectionReference
    ) {

        if (!termsAndCondition.isChecked) {
            Toast.makeText(
                requireContext(),
                "Kindly, accept our Terms & Condition before proceeding", Toast.LENGTH_SHORT
            ).show()
            termsAndConditionView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            return
        } else {
            termsAndConditionView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_dark
                )
            )
        }

        if (!isImageChanged) {
            changeColor(android.R.color.holo_red_dark)
            profilePicInstruction.requestFocus()
            return
        } else {
            profilePicInstruction.text = "Yay! What a great image."
            changeColor(android.R.color.holo_green_dark)
        }

        val fullName = fullNameEditText.text.toString()
        if (emptyValidation(fullName, fullNameInputLayout)) return

        val email = emailEditText.text.toString()
        if (emailValidation(email, emailInputLayout)) return

        val password = passwordEditText.text.toString()
        if (passwordValidation(password, passwordInputLayout)) return

        val confirmPassword = confirmPasswordEditText.text.toString()
        if (confirmPasswordValidation(confirmPassword, password, confirmPasswordInputLayout)) return

        val userModel = User()
        userModel.fullName = fullName
        userModel.email = email
        userModel.password = confirmPassword
        userModel.wallet = 0.0
        userModel.completeAddress = ""
        userModel.houseNumber = ""
        userModel.blockNumber = ""
        userModel.contact = ""
        userModel.gender = 0
        userModel.favoriteItems = emptyList()

        auth.createUserWithEmailAndPassword(email, confirmPassword)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.isComplete) {
                    Log.d(LOG_TAG, "User creation successful & completed!")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .setPhotoUri(selectedImage)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful && updateTask.isComplete) {
                                Log.d(LOG_TAG, "User profile updated")
                                userModel.userId = user.uid
                                userCollection.document(user.uid).set(userModel)
                                    .addOnCompleteListener {
                                        if (task.isComplete && task.isSuccessful) {
                                            navController.navigate(R.id.action_registerFragment_to_navigation_user)
                                        } else {
                                            Log.d(LOG_TAG, "Saving user data failed")
                                        }
                                    }
                                    .addOnFailureListener {
                                        Log.d(LOG_TAG, it.localizedMessage, it)
                                    }
                            }
                        }
                } else {
                    Log.d(LOG_TAG, "User creation unsuccessful!")
                }
            }
            .addOnFailureListener { e ->
                try {
                    throw e
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(requireContext(), "User already exists", Toast.LENGTH_SHORT)
                        .show()
                    emailEditText.requestFocus()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }


    private fun findingViews(root: View) {
        root.run {
            fullNameInputLayout = findViewById(R.id.full_name_input_layout)
            emailInputLayout = findViewById(R.id.email_input_layout)
            passwordInputLayout = findViewById(R.id.password_input_layout)
            confirmPasswordInputLayout = findViewById(R.id.confirm_password_input_layout)
            confirmPasswordEditText = findViewById(R.id.confirm_password_input_edit_text)
            passwordEditText = findViewById(R.id.password_input_edit_text)
            emailEditText = findViewById(R.id.email_input_edit_text)
            fullNameEditText = findViewById(R.id.full_name_edit_text)
            imageView = findViewById(R.id.profile_pic)
            termsAndCondition = findViewById(R.id.terms_and_condition)
            registerButton = findViewById(R.id.register_button)
            termsAndConditionView = findViewById(R.id.terms_and_condition_view)
            profilePicInstruction = findViewById(R.id.profile_pic_instruction)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, image: Intent?) {
        when (requestCode) {
            GALLERY_INTENT -> {
                if (resultCode == RESULT_OK && image?.data != null) {
                    val fileInputStream = image.data?.let {
                        requireContext().contentResolver.openInputStream(it)
                    }
                    val dataSize = fileInputStream?.available()
                    if (dataSize != null && dataSize > IMAGE_SIZE) {
                        changeColor(android.R.color.holo_red_dark)
                    } else {
                        selectedImage = image.data
                        Glide.with(requireActivity()).load(selectedImage).into(imageView)
                        isImageChanged = true
                        profilePicInstruction.text = "Yay! What a great image."
                        changeColor(android.R.color.holo_green_dark)
                    }
                }
            }
        }
    }

    private fun changeColor(@ColorRes color: Int) {
        profilePicInstruction.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}
