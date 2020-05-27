package com.designbyark.layao.ui.categories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Products
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.ui.productList.ProductListAdapter
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class CPListFragment : Fragment(),
    ProductListAdapter.ProductListItemClickListener {

    private var categoryId: String? = null

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
        setHasOptionsMenu(true)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)
//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setTitle(categoryId?.toUpperCase(Locale.ENGLISH))
//        }

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
        val recyclerView: RecyclerView = root.findViewById(R.id.mProductListRV)

        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = ProductListAdapter(options, R.layout.body_product_list, this)

        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
            )
        )

        recyclerView.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun mProductListItemClickListener(productData: MutableMap<String, String>) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productData["id"])
        args.putString(HomeFragment.PRODUCT_TAG, productData["tag"])
        Navigation.createNavigateOnClickListener(
            R.id.action_CPListFragment_to_productDetailFragment,
            args
        )
    }


}
