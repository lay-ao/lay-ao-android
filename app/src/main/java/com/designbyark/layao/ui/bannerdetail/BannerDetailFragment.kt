package com.designbyark.layao.ui.bannerdetail


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.BANNER_COLLECTION
import com.designbyark.layao.common.formatDate
import com.designbyark.layao.data.Banner
import com.designbyark.layao.databinding.FragmentBannerDetailBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class BannerDetailFragment : Fragment() {

    val args: BannerDetailFragmentArgs by navArgs()

    private lateinit var promoCode: String
    private lateinit var binding: FragmentBannerDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_banner_detail, container, false)
        binding.detail = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(BANNER_COLLECTION)
        val document = args.bannerId.let { collection.document(it) }

        getData(view, document)
    }

    private fun getData(view: View, document: DocumentReference?) {
        document?.get()?.addOnSuccessListener { documentSnapshot ->
            val model = documentSnapshot.toObject(Banner::class.java)
            if (model != null) {
                Glide.with(requireActivity()).load(model.image).into(binding.mImage)
                binding.mTitle.text = model.title
                binding.mDescription.text = model.description
                binding.mValidity.text = model.validity?.let { "Valid till ${formatDate(it)}" }
                binding.mPromoCode.text = String.format("Promo Code: %s", model.code)
                promoCode = model.code
            }
        }?.addOnFailureListener { exception ->
            Log.d("Banner Detail: ", "get failed with ", exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    fun copyPromoCode() {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("promo", promoCode))
        Toast.makeText(requireContext(), "Promo Code Copied!", Toast.LENGTH_SHORT).show()
    }

}
