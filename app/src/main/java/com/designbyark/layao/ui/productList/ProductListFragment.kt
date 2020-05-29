package com.designbyark.layao.ui.productList


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.adapters.ProductListAdapter
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

    private var mAdapter: ProductListAdapter? = null

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

        (requireActivity() as AppCompatActivity).run {
            when {
                brandId != null -> supportActionBar?.setTitle(brandId)
                newArrivalId != null -> supportActionBar?.setTitle("New Arrival")
                else -> supportActionBar?.setTitle("Items on Discount")
            }
        }
        setHasOptionsMenu(true)

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

        brandId = ""
        newArrivalId = ""
        discountId = ""
    }

    private fun getData(view: View, query: Query) {
        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = ProductListAdapter(options, this)

        view.mProductListRV.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
            )
        )

        view.mProductListRV.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun mProductListItemClickListener(productData: MutableMap<String, String>) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productData["id"])
        args.putString(HomeFragment.PRODUCT_TAG, productData["tag"])
        Navigation.createNavigateOnClickListener(
            R.id.action_productListFragment_to_productDetailFragment,
            args
        )
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null){
            mAdapter?.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null){
            mAdapter?.stopListening()
        }
    }
}
