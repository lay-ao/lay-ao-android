package com.designbyark.layao.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Category
import com.designbyark.layao.ui.home.banners.BannerAdapter
import com.designbyark.layao.ui.home.brands.BrandsAdapter
import com.designbyark.layao.ui.home.discountItems.DiscountItemsAdapter
import com.designbyark.layao.ui.home.newArrival.NewArrivalAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var mBannerAdapter: BannerAdapter
    private lateinit var mDiscountItemsAdapter: DiscountItemsAdapter
    private lateinit var mNewArrivalAdapter: NewArrivalAdapter
    private lateinit var mBrandsAdapter: BrandsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val productsCollection = firestore.collection(PRODUCTS_COLLECTION)

        getBannerData(root, firestore)
        getDiscountItemsData(root, productsCollection)
        getNewArrivalData(root, productsCollection)
        getBrandsData(root, firestore)

        return root
    }

    private fun getBannerData(root: View, firestore: FirebaseFirestore) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.banner_recycler_view)

        // Getting collection reference from firestore
        val collection = firestore.collection(BANNER_COLLECTION)

        // Applying query to collection reference
        val query = collection.whereEqualTo(ACTIVE, true)
            .orderBy("validity", Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Banner>()
            .setQuery(query, Banner::class.java)
            .build()

        // Assigning adapter class
        mBannerAdapter = BannerAdapter(options)

        // Assigning adapter to Recycler View
        setListLayout(recyclerView, requireContext())
        recyclerView.adapter = mBannerAdapter
    }

    private fun getDiscountItemsData(root: View, collection: CollectionReference) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.discount_item_recycler_view)

        // Applying query to collection reference
        val query = collection.whereGreaterThan(DISCOUNT, 0)
            .orderBy(DISCOUNT, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        // Assigning adapter class
        mDiscountItemsAdapter = DiscountItemsAdapter(options, requireContext())

        // Assigning adapter to Recycler View
        setListLayout(recyclerView, requireContext())
        recyclerView.adapter = mDiscountItemsAdapter
    }

    private fun getNewArrivalData(root: View, collection: CollectionReference) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.new_arrival_recycler_view)

        // Applying query to collection reference
        val query = collection.whereEqualTo(NEW_ARRIVAL, true)
            .orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        // Assigning adapter class
        mNewArrivalAdapter = NewArrivalAdapter(options, requireContext())

        // Assigning adapter to Recycler View
        setListLayout(recyclerView, requireActivity())
        recyclerView.adapter = mNewArrivalAdapter
    }

    private fun getBrandsData(root: View, firestore: FirebaseFirestore) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.brands_recycler_view)

        // Getting collection reference from firestore
        val collection = firestore.collection(BRANDS_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        // Assigning adapter class
        mBrandsAdapter = BrandsAdapter(options, requireContext())

        // Assigning adapter to Recycler View
        setListLayout(recyclerView, requireContext())
        recyclerView.adapter = mBrandsAdapter

    }

    override fun onStart() {
        super.onStart()
        mBannerAdapter.startListening()
        mDiscountItemsAdapter.startListening()
        mNewArrivalAdapter.startListening()
        mBrandsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mBannerAdapter.stopListening()
        mDiscountItemsAdapter.stopListening()
        mNewArrivalAdapter.stopListening()
        mBrandsAdapter.stopListening()
    }
}