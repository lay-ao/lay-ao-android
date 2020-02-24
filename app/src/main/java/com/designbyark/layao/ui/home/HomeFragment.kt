package com.designbyark.layao.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Category
import com.designbyark.layao.ui.home.banners.BannerSliderAdapter
import com.designbyark.layao.ui.home.banners.HomeCategoriesAdapter
import com.designbyark.layao.ui.home.brands.BrandsAdapter
import com.designbyark.layao.ui.home.discountItems.DiscountItemsAdapter
import com.designbyark.layao.ui.home.newArrival.NewArrivalAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class HomeFragment : Fragment(),
    DiscountItemsAdapter.DiscountItemClickListener,
    NewArrivalAdapter.NewArrivalClickListener,
    BrandsAdapter.BrandItemClickListener,
    BannerSliderAdapter.BannerItemClickListener,
    HomeCategoriesAdapter.HomeCategoryItemClickListener {

    companion object {
        const val BANNER_ID = "bannerId"
        const val PRODUCT_ID = "productId"
        const val BRAND_ID = "brandId"
        const val CATEGORY_ID = "categoryId"
        const val PASSED_ID = "passedId"
    }

    private var mHomeCategoryAdapter: HomeCategoriesAdapter? = null
    private var mDiscountItemsAdapter: DiscountItemsAdapter? = null
    private var mNewArrivalAdapter: NewArrivalAdapter? = null
    private var mBrandsAdapter: BrandsAdapter? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val favoritesButton: ImageButton = root.findViewById(R.id.favorites_button)
        favoritesButton.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_favoritesFragment)
        }

        val viewAllCategories: TextView = root.findViewById(R.id.view_all_categories)
        val moreDiscountItems: TextView = root.findViewById(R.id.more_discount_items)
        val moreNewArrivals: TextView = root.findViewById(R.id.more_new_arrival)
        val moreBrands: TextView = root.findViewById(R.id.more_brands)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val productsCollection = firestore.collection(PRODUCTS_COLLECTION)

        getBannerData(root, firestore, requireContext())
        getHomeCategoryData(root, firestore)
        getDiscountItemsData(root, productsCollection)
        getNewArrivalData(root, productsCollection)
        getBrandsData(root, firestore)

        moreDiscountItems.setOnClickListener {
            navController.navigate(R.id.action_nav_productListFragment)
        }

        moreNewArrivals.setOnClickListener {
            val args = Bundle()
            args.putString(PASSED_ID, "newArrival")
            navController.navigate(R.id.action_nav_productListFragment, args)
        }

        moreBrands.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_brandsListFragment)
        }

        viewAllCategories.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_navigation_category)
        }

        return root
    }

    private fun getBannerData(root: View, firestore: FirebaseFirestore, context: Context) {

        // Getting SliderView
        val sliderView: SliderView = root.findViewById(R.id.banner_image_slider)

        // Getting collection reference from firestore
        val collection = firestore.collection(BANNER_COLLECTION)

        // Applying query to collection reference
        val query = collection
            .whereEqualTo(ACTIVE, true)
            .orderBy("validity", Query.Direction.ASCENDING)

        query.get().addOnSuccessListener { documents ->
            val bannerList: ArrayList<Banner> = arrayListOf()
            for (document in documents) {
                val banner = document.toObject(Banner::class.java)
                bannerList.add(banner)
            }
            sliderView.sliderAdapter = BannerSliderAdapter(context, bannerList, this)
        }

        sliderView.startAutoCycle()
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
    }

    private fun getHomeCategoryData(root: View, firestore: FirebaseFirestore) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.home_categories_recycler_view)

        // Get Banner Collection Reference from Firestore
        val collection = firestore.collection(CATEGORIES_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Banner>()
            .setQuery(query, Banner::class.java)
            .build()

        mHomeCategoryAdapter = HomeCategoriesAdapter(options, this, requireContext())

        // Assigning adapter to Recycler View
        setHorizontalListLayout(recyclerView, requireContext())
        recyclerView.adapter = mHomeCategoryAdapter

    }

    private fun getDiscountItemsData(root: View, collection: CollectionReference) {

        // Capturing recycler view
        val recyclerView: RecyclerView = root.findViewById(R.id.discount_item_recycler_view)

        // Applying query to collection reference
        val query = collection.whereGreaterThan(DISCOUNT, 0)
            .orderBy(DISCOUNT, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        mDiscountItemsAdapter = DiscountItemsAdapter(options, requireContext(), this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(recyclerView, requireContext())
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

        mNewArrivalAdapter = NewArrivalAdapter(options, requireContext(), this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(recyclerView, requireActivity())
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

        mBrandsAdapter = BrandsAdapter(options, requireContext(), this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(recyclerView, requireContext())
        recyclerView.adapter = mBrandsAdapter
    }

    override fun onStart() {
        super.onStart()

        if (mHomeCategoryAdapter != null) {
            mHomeCategoryAdapter?.startListening()
        }

        if (mDiscountItemsAdapter != null) {
            mDiscountItemsAdapter?.startListening()
        }

        if (mNewArrivalAdapter != null) {
            mNewArrivalAdapter?.startListening()
        }

        if (mBrandsAdapter != null) {
            mBrandsAdapter?.startListening()
        }

    }

    override fun onStop() {
        super.onStop()

        if (mHomeCategoryAdapter != null) {
            mHomeCategoryAdapter?.stopListening()
        }

        if (mDiscountItemsAdapter != null) {
            mDiscountItemsAdapter?.stopListening()
        }

        if (mNewArrivalAdapter != null) {
            mNewArrivalAdapter?.stopListening()
        }

        if (mBrandsAdapter != null) {
            mBrandsAdapter?.stopListening()
        }

    }

    override fun onDiscountItemClickListener(productId: String) {
        val args = Bundle()
        args.putString(PRODUCT_ID, productId)
        navController.navigate(R.id.action_nav_productDetailFragment, args)
    }

    override fun mNewArrivalClickListener(productId: String) {
        val args = Bundle()
        args.putString(PRODUCT_ID, productId)
        navController.navigate(R.id.action_nav_productDetailFragment, args)
    }

    override fun mBrandItemClickListener(brandId: String) {
        val args = Bundle()
        args.putString(BRAND_ID, brandId)
        navController.navigate(R.id.action_nav_productListFragment, args)
    }

    override fun mBannerItemClickListener(bannerId: String?) {
        val args = Bundle()
        args.putString(BANNER_ID, bannerId)
        navController.navigate(R.id.action_nav_bannerDetailFragment, args)
    }

    override fun onHomeCategoryItemClickListener(categoryId: String) {
        val args = Bundle()
        args.putString(HomeFragment.CATEGORY_ID, categoryId)
        navController.navigate(R.id.action_navigation_home_to_CPListFragment, args)
    }

}

