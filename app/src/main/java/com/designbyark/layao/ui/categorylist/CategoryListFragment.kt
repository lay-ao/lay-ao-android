package com.designbyark.layao.ui.categorylist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.ProductListAdapter
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.FragmentProductListBinding
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class CategoryListFragment : Fragment(),
    ProductListAdapter.ProductListItemClickListener {

    private var categoryId: String? = null

    private lateinit var binding: FragmentProductListBinding
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle(categoryId?.toUpperCase(Locale.ENGLISH))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)

        val query = collection.whereEqualTo("tag", categoryId)
            .orderBy(TITLE, Query.Direction.ASCENDING)

        getData(query)
    }

    private fun getData(query: Query) {

        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = ProductListAdapter(options, this)

        binding.mProductListRV.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
            )
        )

        binding.mProductListRV.adapter = mAdapter
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
        findNavController().navigate(
            R.id.action_CPListFragment_to_productDetailFragment,
            args
        )
    }


}