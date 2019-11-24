package com.designbyark.layao.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.ACTIVE
import com.designbyark.layao.common.BANNER_COLLECTION
import com.designbyark.layao.common.DISCOUNT
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Product
import com.designbyark.layao.ui.home.banner.BannerAdapter
import com.designbyark.layao.ui.home.discountItems.DiscountItemsAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var mBannerAdapter: BannerAdapter
    private lateinit var mDiscountItemsAdapter: DiscountItemsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getBannerData(root)
        getDiscountItemsData(root)

        return root
    }

    private fun getBannerData(root: View) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.banner_recycler_view)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val collection = firestore.collection(BANNER_COLLECTION)

        // Applying query to collection reference
        val query = collection.whereEqualTo(ACTIVE, true)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Banner>()
            .setQuery(query, Banner::class.java)
            .build()

        // Assigning adapter class
        mBannerAdapter = BannerAdapter(options)

        // Assigning adapter to Recycler View
        val layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mBannerAdapter
    }

    private fun getDiscountItemsData(root: View) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.discount_item_recycler_view)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val collection = firestore.collection(PRODUCTS_COLLECTION)

        // Applying query to collection reference
        val query = collection.whereGreaterThan(DISCOUNT, 0)
            .orderBy(DISCOUNT, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()

        // Assigning adapter class
        mDiscountItemsAdapter = DiscountItemsAdapter(options, requireContext())

        // Assigning adapter to Recycler View
        val layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mDiscountItemsAdapter
    }

    override fun onStart() {
        super.onStart()
        mBannerAdapter.startListening()
        mDiscountItemsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mBannerAdapter.stopListening()
        mDiscountItemsAdapter.stopListening()
    }
}