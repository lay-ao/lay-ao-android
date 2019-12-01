package com.designbyark.layao.ui.categories


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Product
import com.designbyark.layao.helper.MarginItemDecoration
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.ui.productList.ProductListAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class CPListFragment : Fragment(),
    ProductListAdapter.ProductListItemClickListener{

    private var categoryId: String? = null

    private lateinit var navController: NavController
    private lateinit var mAdapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getString(HomeFragment.CATEGORY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)


        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(categoryId?.toUpperCase(Locale.ENGLISH))
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val root = inflater.inflate(R.layout.fragment_product_list, container, false)

        val query = collection.whereEqualTo("tag", categoryId)
            .orderBy(TITLE, Query.Direction.ASCENDING)

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

        mAdapter = ProductListAdapter(options, requireActivity(), this)

        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )

        recyclerView.adapter = mAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun mProductListItemClickListener(productId: String) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productId)
        navController.navigate(R.id.action_CPListFragment_to_productDetailFragment, args)
    }


}
