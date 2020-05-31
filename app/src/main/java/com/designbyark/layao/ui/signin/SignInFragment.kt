package com.designbyark.layao.ui.signin


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.databinding.FragmentSignInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.view.*

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        binding.signin = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomMenu.visibility = View.GONE

    }

    fun forgotPassword() {
        findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
    }

    fun signIn() {
        if (!isConnectedToInternet(requireContext())) {
            Log.e(LOG_TAG, "Not connected to the internet!")
            return
        }

        val email = binding.mEmailET.text.toString()
        if (emailValidation(email, binding.mEmailIL)) return

        val password = binding.mPasswordET.text.toString()
        if (passwordValidation(password, binding.mPasswordIL)) return

        disableInteraction(requireActivity(), binding.mIncludeProgressBar)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isComplete && task.isSuccessful) {
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    findNavController().navigate(R.id.action_signInFragment_to_navigation_user)
                } else {
                    Log.e(
                        LOG_TAG,
                        "signInWithEmailAndPassword -> addOnCompleteListener: ${task.exception?.localizedMessage}"
                    )
                    enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                Log.e(
                    LOG_TAG,
                    "signInWithEmailAndPassword -> addOnFailureListener: ${it.localizedMessage}"
                )
                enableInteraction(requireActivity(), binding.mIncludeProgressBar)
                return@addOnFailureListener
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }


}
