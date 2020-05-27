package com.designbyark.layao.ui.home.bannerdetail


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.BANNER_COLLECTION
import com.designbyark.layao.common.formatDate
import com.designbyark.layao.data.Banner
import com.designbyark.layao.ui.home.HomeFragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_banner_detail.view.*


class BannerDetailFragment : Fragment() {

    private var bannerId: String? = null

    private lateinit var promoCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bannerId = it.getString(HomeFragment.BANNER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_banner_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
//        }

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(BANNER_COLLECTION)
        val document = bannerId?.let { collection.document(it) }

        getData(view, document)

        view.mPromoCode.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("promo", promoCode))
            Toast.makeText(requireContext(), "Promo Code Copied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getData(view: View, document: DocumentReference?) {
        document?.get()?.addOnSuccessListener { documentSnapshot ->
            val model = documentSnapshot.toObject(Banner::class.java)
            if (model != null) {
                Glide.with(requireActivity()).load(model.image).into(view.mImage)
                view.mTitle.text = model.title
                view.mDescription.text = model.description
                view.mValidity.text = model.validity?.let { "Valid till ${formatDate(it)}" }
                view.mPromoCode.text = String.format("Promo Code: %s", model.code)
                promoCode = model.code
            }
        }?.addOnFailureListener { exception ->
            Log.d("Banner Detail: ", "get failed with ", exception)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
