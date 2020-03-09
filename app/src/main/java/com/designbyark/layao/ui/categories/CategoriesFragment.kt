package com.designbyark.layao.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.CATEGORIES_COLLECTION
import com.designbyark.layao.common.TITLE
import com.designbyark.layao.data.Category
import com.designbyark.layao.util.MarginItemDecoration
import com.designbyark.layao.ui.home.HomeFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_categories.view.*

class CategoriesFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private lateinit var navController: NavController
    private lateinit var mAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment)

        view.mBackNav.setOnClickListener { navController.navigateUp() }

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
        mAdapter = CategoryAdapter(options, requireActivity(), this)

        // Applying item decoration to recycler view components
        view.mCategoryRV.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )

        // Assigning adapter to Recycler View
        view.mCategoryRV.adapter = mAdapter

    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    override fun mCategoryClickListener(categoryId: String) {
        val args = Bundle()
        args.putString(HomeFragment.CATEGORY_ID, categoryId)
        navController.navigate(R.id.action_navigation_category_to_CPListFragment, args)
    }
}