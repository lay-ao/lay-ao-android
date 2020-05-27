package com.designbyark.layao.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Category
import com.designbyark.layao.adapters.BannerSliderAdapter
import com.designbyark.layao.ui.home.banners.CategoriesAdapter
import com.designbyark.layao.adapters.BrandsAdapter
import com.designbyark.layao.adapters.DiscountItemsAdapter
import com.designbyark.layao.adapters.NewArrivalAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(),
    DiscountItemsAdapter.DiscountItemClickListener,
    NewArrivalAdapter.NewArrivalClickListener,
    BrandsAdapter.BrandItemClickListener,
    BannerSliderAdapter.BannerItemClickListener,
    CategoriesAdapter.CategoryItemClickListener {

    companion object {
        const val BANNER_ID = "bannerId"
        const val PRODUCT_ID = "productId"
        const val PRODUCT_TAG = "productTag"
        const val BRAND_ID = "brandId"
        const val CATEGORY_ID = "categoryId"
        const val PASSED_ID = "passedId"
        const val DISCOUNT_ID = "discountId"
    }

    private var mHomeCategoryAdapter: CategoriesAdapter? = null
    private var mDiscountItemsAdapter: DiscountItemsAdapter? = null
    private var mNewArrivalAdapter: NewArrivalAdapter? = null
    private var mBrandsAdapter: BrandsAdapter? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setSubtitle(getString(R.string.app_slogan))
//        }

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        var user = auth.currentUser

        user?.reload()?.addOnSuccessListener {
            user = auth.currentUser
            if (!user?.isEmailVerified!!) {
                view.mEmailVerified.visibility = View.VISIBLE
            }
        }

        // Getting collection reference from firestore
        val productsCollection = firestore.collection(PRODUCTS_COLLECTION)

        getBannerData(view, firestore)
        getCategoryData(view, firestore)
        getDiscountItemsData(view, productsCollection)
        getNewArrivalData(view, productsCollection)
        getBrandsData(view, firestore)

        view.mAllDiscountItems.setOnClickListener {
            val args = Bundle()
            args.putString(DISCOUNT_ID, "discountId")
            navController.navigate(R.id.action_nav_productListFragment, args)
        }

        view.mAllNewArrival.setOnClickListener {
            val args = Bundle()
            args.putString(PASSED_ID, "newArrival")
            navController.navigate(R.id.action_nav_productListFragment, args)
        }

        view.mAllBrands.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_brandsListFragment)
        }

        view.mAllCategories.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_navigation_category)
        }

    }

    private fun getBannerData(view: View, firestore: FirebaseFirestore) {

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
            view.mBannerImageSlider.sliderAdapter =
                BannerSliderAdapter(
                    bannerList,
                    this
                )
        }

        view.mBannerImageSlider.startAutoCycle()
        view.mBannerImageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
        view.mBannerImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
    }

    private fun getCategoryData(view: View, firestore: FirebaseFirestore) {

        // Get Banner Collection Reference from Firestore
        val collection = firestore.collection(CATEGORIES_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        mHomeCategoryAdapter = CategoriesAdapter(options, this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(view.mHomeCategoriesRV, requireContext())
        view.mHomeCategoriesRV.adapter = mHomeCategoryAdapter

    }

    private fun getDiscountItemsData(view: View, collection: CollectionReference) {

        // Applying query to collection reference
        val query = collection.whereGreaterThan(DISCOUNT, 0)
            .orderBy(DISCOUNT, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        mDiscountItemsAdapter =
            DiscountItemsAdapter(
                options,
                requireContext(),
                this
            )

        // Assigning adapter to Recycler View
        setHorizontalListLayout(view.mDiscountItemRV, requireContext())
        view.mDiscountItemRV.adapter = mDiscountItemsAdapter
    }

    private fun getNewArrivalData(view: View, collection: CollectionReference) {

        // Applying query to collection reference
        val query = collection.whereEqualTo(NEW_ARRIVAL, true)
            .orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        mNewArrivalAdapter = NewArrivalAdapter(
            options,
            requireContext(),
            this
        )

        // Assigning adapter to Recycler View
        setHorizontalListLayout(view.mNewArrivalRV, requireActivity())
        view.mNewArrivalRV.adapter = mNewArrivalAdapter
    }

    private fun getBrandsData(view: View, firestore: FirebaseFirestore) {

        // Getting collection reference from firestore
        val collection = firestore.collection(BRANDS_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        mBrandsAdapter = BrandsAdapter(
            options,
            requireContext(),
            this
        )

        // Assigning adapter to Recycler View
        setHorizontalListLayout(view.mBrandsRV, requireContext())
        view.mBrandsRV.adapter = mBrandsAdapter
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

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setSubtitle(null)
        }

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

    override fun onDiscountItemClickListener(productData: MutableMap<String, String>) {
        val args = Bundle()
        args.putString(PRODUCT_ID, productData["id"])
        args.putString(PRODUCT_TAG, productData["tag"])
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

    override fun onCategoryItemClickListener(categoryId: String) {
        val args = Bundle()
        args.putString(CATEGORY_ID, categoryId)
        navController.navigate(R.id.action_navigation_home_to_CPListFragment, args)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.general_favorites -> {
//                navController.navigate(R.id.home_to_favorites_dest)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

}

