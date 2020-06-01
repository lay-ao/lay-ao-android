package com.designbyark.layao.ui.productList


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.adapters.ProductListAdapter
import com.designbyark.layao.common.DISCOUNT
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.FragmentProductListBinding
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProductListFragment : Fragment(), ProductListAdapter.ProductListItemClickListener {

    private val brandArgs: ProductListFragmentArgs by navArgs()

    private var newArrivalId: String? = null
    private var discountId: String? = null

    private var mAdapter: ProductListAdapter? = null
    private lateinit var binding: FragmentProductListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newArrivalId = it.getString(HomeFragment.PASSED_ID)
            discountId = it.getString(HomeFragment.DISCOUNT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            when {
                brandArgs.brandId != "NIP" -> supportActionBar?.setTitle(brandArgs.brandId)
                newArrivalId != null -> supportActionBar?.setTitle("New Arrival")
                else -> supportActionBar?.setTitle("Items on Discount")
            }
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        setHasOptionsMenu(true)

        val query: Query?

        when {
            brandArgs.brandId != "NIP" -> {
                query = collection.whereEqualTo("brand", brandArgs.brandId)
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

        // brandId = ""
        newArrivalId = ""
        discountId = ""
    }

    private fun getData(view: View, query: Query) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun mProductListItemClickListener(product: Products) {
        val action =
            ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(product)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null) {
            mAdapter?.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter?.stopListening()
        }
    }
}
