package com.designbyark.layao.ui.categories

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.adapters.CategoryAdapter
import com.designbyark.layao.common.CATEGORIES_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Category
import com.designbyark.layao.ui.home.HomeFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_categories.view.*

class CategoriesFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private var mAdapter: CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setHomeButtonEnabled(true)
//            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
//        }

        // Getting firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Getting collection reference from firestore
        val collection = firestore.collection(CATEGORIES_COLLECTION)

        // Applying query to collection reference
        val query = collection.orderBy(TITLE, Query.Direction.ASCENDING)

        // Setting query with model class
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        // Assigning adapter class
        mAdapter = CategoryAdapter(
            options,
            requireActivity(),
            this
        )

        // Assigning adapter to Recycler View
        view.mCategoryRV.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
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

    override fun mCategoryClickListener(categoryId: String) {
        val args = Bundle()
        args.putString(HomeFragment.CATEGORY_ID, categoryId)
        Navigation.createNavigateOnClickListener(
            R.id.action_navigation_category_to_CPListFragment,
            args
        )
    }
}