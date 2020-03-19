package com.designbyark.layao.ui.productList


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.DISCOUNT
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Products
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_product_list.view.*


class ProductListFragment : Fragment(), ProductListAdapter.ProductListItemClickListener {

    private var brandId: String? = null
    private var newArrivalId: String? = null
    private var discountId: String? = null

    private lateinit var navController: NavController
    private lateinit var mAdapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brandId = it.getString(HomeFragment.BRAND_ID)
            newArrivalId = it.getString(HomeFragment.PASSED_ID)
            discountId = it.getString(HomeFragment.DISCOUNT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            if (brandId != null) {
//                supportActionBar?.setTitle(brandId?.toUpperCase(Locale.ENGLISH))
//            } else if (newArrivalId != null) {
//                supportActionBar?.setTitle("New Arrival")
//            } else {
//                supportActionBar?.setTitle("Items on Discount")
//            }
//        }
//        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val query: Query?

        when {
            brandId != null -> {
                query = collection.whereEqualTo("brand", brandId)
                    .orderBy(TITLE, Query.Direction.ASCENDING)
            }
            newArrivalId != null -> {
                query = collection.whereEqualTo("newArrival", true)
                    .orderBy(TITLE, Query.Direction.ASCENDING)
            }
            discountId != null -> {
                query = collection.whereGreaterThan(DISCOUNT, 0)
                    .orderBy(DISCOUNT, Query.Direction.ASCENDING)
            }
            else -> {
                query = collection.orderBy(TITLE, Query.Direction.ASCENDING)
            }
        }

        getData(view, query)
    }

    private fun getData(
        view: View,
        query: Query
    ) {
        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = ProductListAdapter(options, R.layout.body_product_list,this)

        view.mProductListRV.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
            )
        )

        view.mProductListRV.adapter = mAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Clearing all passed ids
                brandId = ""
                newArrivalId = ""
                discountId = ""
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun mProductListItemClickListener(productData: MutableMap<String, String>) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productData["id"])
        args.putString(HomeFragment.PRODUCT_TAG, productData["tag"])
        navController.navigate(R.id.action_productListFragment_to_productDetailFragment, args)
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }
}
