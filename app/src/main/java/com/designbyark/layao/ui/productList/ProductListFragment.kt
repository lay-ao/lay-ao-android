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
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class ProductListFragment : Fragment(), ProductListAdapter.ProductListItemClickListener {

    private val args: ProductListFragmentArgs by navArgs()


    private var mAdapter: ProductListAdapter? = null
    private lateinit var binding: FragmentProductListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        return binding.root
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)

        val title: String
        when {
            args.brandId != null -> title = args.brandId?.capitalize(Locale.getDefault())!!
            args.newArrival != null -> title = "New Arrival"
            args.discountId != null -> title = "Items on Discount"
            else -> title = "Products"
        }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle(title)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        setHasOptionsMenu(true)

        val query: Query?

        when {
            args.brandId != null -> {
                query = collection.whereEqualTo("brand", args.brandId)
                    .orderBy(TITLE, Query.Direction.ASCENDING)
            }
            args.newArrival != null -> {
                query = collection.whereEqualTo("newArrival", true)
                    .orderBy(TITLE, Query.Direction.ASCENDING)
            }
            args.discountId != null -> {
                query = collection.whereGreaterThan(DISCOUNT, 0)
                    .orderBy(DISCOUNT, Query.Direction.ASCENDING)
            }
            else -> {
                query = collection.orderBy(TITLE, Query.Direction.ASCENDING)
            }
        }

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
