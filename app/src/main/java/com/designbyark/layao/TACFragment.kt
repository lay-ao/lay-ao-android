package com.designbyark.layao

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.designbyark.layao.databinding.FragmentTermsAndConditionBinding
import com.designbyark.layao.util.LOG_TAG
import com.google.android.gms.common.util.IOUtils
import io.grpc.internal.IoUtils
import java.io.BufferedReader

class TACFragment : Fragment() {

    private lateinit var binding: FragmentTermsAndConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.show()
        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_terms_and_condition,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().assets.open("terms_and_conditions.html")
            .bufferedReader().use {
                binding.termsAndConditions.text =
                    HtmlCompat.fromHtml(it.readText(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
    }

    override fun onDestroyView() {

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.hide()
        }

        super.onDestroyView()
    }

}