package com.designbyark.layao.ui.productList


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Product
import com.designbyark.layao.helper.MarginItemDecoration
import com.designbyark.layao.ui.favorites.FavoriteViewModel
import com.designbyark.layao.ui.home.HomeFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class ProductListFragment : Fragment(), ProductListAdapter.ProductListItemClickListener {

    private var brandId: String? = null
    private var newArrivalId: String? = null

    private lateinit var navController: NavController
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var mAdapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brandId = it.getString(HomeFragment.BRAND_ID)
            newArrivalId = it.getString(HomeFragment.PASSED_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)
        favoriteViewModel = ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            if (brandId != null) {
                supportActionBar?.setTitle(brandId?.toUpperCase(Locale.ENGLISH))
            } else if (newArrivalId != null) {
                supportActionBar?.setTitle("New Arrival")
            } else {
                supportActionBar?.setTitle("Items on Discount")
            }
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val root = inflater.inflate(R.layout.fragment_product_list, container, false)

        val query: Query?

        if (brandId != null) {
            query = collection.whereEqualTo("brand", brandId)
                .orderBy(TITLE, Query.Direction.ASCENDING)
        } else if (newArrivalId != null) {
            query = collection.whereEqualTo("newArrival", true)
                .orderBy(TITLE, Query.Direction.ASCENDING)
        } else {
            query = collection.orderBy(TITLE, Query.Direction.ASCENDING)
        }

//        val query = collection.whereEqualTo("brand", brandId)
//            .orderBy(TITLE, Query.Direction.ASCENDING)

        getData(root, query)

        return root
    }

    private fun getData(
        root: View,
        query: Query
    ) {
        val recyclerView: RecyclerView = root.findViewById(R.id.product_list_recycler_view)

        val options = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()

        mAdapter = ProductListAdapter(options, requireActivity(), this, favoriteViewModel)

        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )

        recyclerView.adapter = mAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Clearing all passed ids
                brandId = ""
                newArrivalId = ""
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun mProductListItemClickListener(productId: String) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productId)
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
