package com.designbyark.layao.ui.brands


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.BRANDS_COLLECTION
import com.designbyark.layao.common.CATEGORIES_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Brands
import com.designbyark.layao.helper.MarginItemDecoration
import com.designbyark.layao.ui.home.HomeFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class BrandsListFragment : Fragment(),
BrandsListAdapter.BrandsItemClickListener {

    private var mBrandsListAdapter: BrandsListAdapter? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle("Brands")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val collection = firestore.collection(BRANDS_COLLECTION)

        val view = inflater.inflate(R.layout.fragment_brands_list, container, false)

        // Capturing recycler view
        val recyclerView: RecyclerView = view.findViewById(R.id.brand_list_recycler_view)


        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Brands>()
            .setQuery(query, Brands::class.java)
            .build()

        // Assigning adapter class
        mBrandsListAdapter = BrandsListAdapter(options, requireActivity(), this)

        // Applying item decoration to recycler view components
        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )

        // Assigning adapter to Recycler View
        recyclerView.adapter = mBrandsListAdapter

        return view
    }

    override fun mBrandsItemClickListener(brandId: String) {
        val args = Bundle()
        args.putString(HomeFragment.BRAND_ID, brandId)
        navController.navigate(R.id.action_brandsListFragment_to_productListFragment, args)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        if (mBrandsListAdapter != null) {
            mBrandsListAdapter?.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBrandsListAdapter != null) {
            mBrandsListAdapter?.stopListening()
        }
    }

}
