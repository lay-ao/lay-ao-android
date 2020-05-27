package com.designbyark.layao.ui.brands


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.BRANDS_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Brands
import com.designbyark.layao.ui.home.HomeFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_brands_list.view.*

class BrandsListFragment : Fragment(),
    BrandsListAdapter.BrandsItemClickListener {

    private var mBrandsListAdapter: BrandsListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_brands_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val collection = firestore.collection(BRANDS_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Brands>()
            .setQuery(query, Brands::class.java)
            .build()

        // Assigning adapter class
        mBrandsListAdapter = BrandsListAdapter(options, this)

        // Assigning adapter to Recycler View
        view.mBrandsRV.adapter = mBrandsListAdapter
    }

    override fun mBrandsItemClickListener(brandId: String) {
        val args = Bundle()
        args.putString(HomeFragment.BRAND_ID, brandId)
        Navigation.createNavigateOnClickListener(
            R.id.action_brandsListFragment_to_productListFragment,
            args
        )
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
