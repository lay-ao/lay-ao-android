package com.designbyark.layao.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.*
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Brand
import com.designbyark.layao.data.Category
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.FragmentHomeBinding
import com.designbyark.layao.util.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

class HomeFragment : Fragment(),
    DiscountItemsAdapter.DiscountItemClickListener,
    NewArrivalAdapter.NewArrivalClickListener,
    BrandsAdapter.BrandItemClickListener,
    BannerSliderAdapter.BannerItemClickListener,
    CategoriesAdapter.CategoryItemClickListener {

    companion object {
        const val PRODUCT_ID = "productId"
        const val DISCOUNT_ID = "discountId"
        const val NEW_ARRIVAL_ID = "newArrivalId"
    }

    private var mHomeCategoryAdapter: CategoriesAdapter? = null
    private var mDiscountItemsAdapter: DiscountItemsAdapter? = null
    private var mNewArrivalAdapter: NewArrivalAdapter? = null
    private var mBrandsAdapter: BrandsAdapter? = null

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.home = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        var user = auth.currentUser

        firestore.collection("Misc").document("store-timing")
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    Log.d(LOG_TAG, exception.localizedMessage, exception)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.d(LOG_TAG, "Misc: snapshot is null!")
                    return@addSnapshotListener
                }

                val openingTime = snapshot.getString("opening")
                val closingTime = snapshot.getString("closing")

                updateServiceTiming(openingTime, closingTime, view)

            }

        user?.reload()?.addOnSuccessListener {
            user = auth.currentUser
            if (!user?.isEmailVerified!!) {
                binding.mEmailVerified.visibility = View.VISIBLE
            }
        }

        // Getting collection reference from firestore
        val productsCollection = firestore.collection(PRODUCTS_COLLECTION)

        getBanners(firestore)
        getCategories(firestore)
        getDiscountItems(productsCollection)
        getNewArrivals(productsCollection)
        getBrands(firestore)

    }

    private fun updateServiceTiming(opening: String?, closing: String?, view: View) {
        val now = LocalTime.now()
        val openingTime = LocalTime.parse(opening, DateTimeFormat.forPattern("hh:mm a"))
        val closingTime = LocalTime.parse(closing, DateTimeFormat.forPattern("hh:mm a"))
        when {
            now < closingTime && now > openingTime -> {
                binding.mServiceStatus.text = getString(R.string.active_service)
                binding.mServiceStatus.background =
                    view.context.getDrawable(R.drawable.green_background)
            }
            else -> {
                binding.mServiceStatus.text = getString(R.string.schedule_orders)
                binding.mServiceStatus.background =
                    view.context.getDrawable(R.drawable.purple_background)
            }
        }
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

    private fun getBanners(firestore: FirebaseFirestore) {

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
            binding.mBannerImageSlider.sliderAdapter =
                BannerSliderAdapter(
                    bannerList,
                    this
                )
        }

        binding.mBannerImageSlider.startAutoCycle()
        binding.mBannerImageSlider.setIndicatorAnimation(IndicatorAnimations.WORM)
        binding.mBannerImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
    }

    private fun getCategories(firestore: FirebaseFirestore) {

        // Get Banner Collection Reference from Firestore
        val collection = firestore.collection(CATEGORIES_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        mHomeCategoryAdapter =
            CategoriesAdapter(options, this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(
            binding.mHomeCategoriesRV,
            requireContext()
        )
        binding.mHomeCategoriesRV.adapter = mHomeCategoryAdapter

    }

    private fun getDiscountItems(collection: CollectionReference) {

        // Applying query to collection reference
        val query = collection.whereGreaterThan(DISCOUNT, 0)
            .orderBy(DISCOUNT, Query.Direction.ASCENDING).limit(5)

        // Setting query with model class
        val options = getProductOptions(query)

        mDiscountItemsAdapter =
            DiscountItemsAdapter(options, this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(
            binding.mDiscountItemRV,
            requireContext()
        )
        binding.mDiscountItemRV.adapter = mDiscountItemsAdapter
    }

    private fun getNewArrivals(collection: CollectionReference) {

        // Applying query to collection reference
        val query = collection.whereEqualTo(NEW_ARRIVAL, true)
            .orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = getProductOptions(query)

        mNewArrivalAdapter = NewArrivalAdapter(options, this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(
            binding.mNewArrivalRV,
            requireActivity()
        )
        binding.mNewArrivalRV.adapter = mNewArrivalAdapter
    }

    private fun getBrands(firestore: FirebaseFirestore) {

        // Getting collection reference from firestore
        val collection = firestore.collection(BRANDS_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Brand>()
            .setQuery(query, Brand::class.java)
            .build()

        mBrandsAdapter = BrandsAdapter(options, this)

        // Assigning adapter to Recycler View
        setHorizontalListLayout(
            binding.mBrandsRV,
            requireContext()
        )
        binding.mBrandsRV.adapter = mBrandsAdapter
    }

    override fun onDiscountItemClickListener(product: Products) {
        val action = HomeFragmentDirections.actionNavProductDetailFragment(product)
        findNavController().navigate(action)
    }

    override fun onDiscountSeeMoreClickListener() {
        val action = HomeFragmentDirections.actionNavProductListFragment(null, DISCOUNT_ID, null)
        findNavController().navigate(action)
    }

    override fun mNewArrivalClickListener(product: Products) {
        val action = HomeFragmentDirections.actionNavProductDetailFragment(product)
        findNavController().navigate(action)
    }

    override fun onNewArrivalSeeMoreClickListener() {
        val action = HomeFragmentDirections.actionNavProductListFragment(null, null, NEW_ARRIVAL_ID)
        findNavController().navigate(action)
    }

    override fun mBrandItemClickListener(brandId: String) {
        val action = HomeFragmentDirections.actionNavProductListFragment(brandId, null, null)
        findNavController().navigate(action)
    }

    override fun onBrandSeeMoreClickListener() {
        findNavController().navigate(R.id.action_navigation_home_to_brandsListFragment)
    }

    override fun mBannerItemClickListener(banner: Banner) {
        val action = HomeFragmentDirections.actionNavBannerDetailFragment(banner)
        findNavController().navigate(action)
    }

    override fun onCategoryItemClickListener(categoryId: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToCPListFragment(categoryId)
        findNavController().navigate(action)
    }

    override fun onCategorySeeMoreClickListener() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_category)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.general_menu, menu)
    }

    fun allDiscountItems() {
        val action = HomeFragmentDirections.actionNavProductListFragment(null, DISCOUNT_ID, null)
        findNavController().navigate(action)
    }

    fun allNewArrivals() {
        val action = HomeFragmentDirections.actionNavProductListFragment(null, null, NEW_ARRIVAL_ID)
        findNavController().navigate(action)
    }

    fun allBrands() {
        findNavController().navigate(R.id.action_navigation_home_to_brandsListFragment)
    }

    fun allCategories() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_category)
    }

}

