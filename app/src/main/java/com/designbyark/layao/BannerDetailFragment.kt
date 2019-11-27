package com.designbyark.layao


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.designbyark.layao.common.BANNER_COLLECTION
import com.designbyark.layao.common.formatTimeDate
import com.designbyark.layao.data.Banner
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class BannerDetailFragment : Fragment() {

    private var bannerId: String? = null
    private lateinit var navController: NavController

    private lateinit var mImage: ImageView
    private lateinit var mTitle: TextView
    private lateinit var mDescription: TextView
    private lateinit var mValidity: TextView
    private lateinit var mPromoCode: TextView
    private lateinit var mCopyCode: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bannerId = it.getString("bannerId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(BANNER_COLLECTION)
        val document = bannerId?.let { collection.document(it) }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        val root = inflater.inflate(R.layout.fragment_banner_detail, container, false)

        findingViews(root)
        getData(document)

        return root
    }

    private fun getData(document: DocumentReference?) {
        document?.get()?.addOnSuccessListener { documentSnapshot ->
            val model = documentSnapshot.toObject(Banner::class.java)
            if (model != null) {
                (requireActivity() as AppCompatActivity).run {
                    supportActionBar?.setTitle(model.title)
                }
                Glide.with(requireActivity()).load(model.image).into(mImage)
                mTitle.text = model.title
                mDescription.text = model.description
                mValidity.text = model.validity?.let { "Valid till ${formatTimeDate(it)}" }
                mPromoCode.text = String.format("Promo Code: %s", model.code)
            }
        }?.addOnFailureListener { exception ->
            Log.d("Banner Detail: ", "get failed with ", exception)
        }
    }

    private fun findingViews(root: View) {
        mImage = root.findViewById(R.id.image)
        mTitle = root.findViewById(R.id.title)
        mDescription = root.findViewById(R.id.description)
        mValidity = root.findViewById(R.id.validity)
        mPromoCode = root.findViewById(R.id.promo_code)
        mCopyCode = root.findViewById(R.id.copy_code)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
