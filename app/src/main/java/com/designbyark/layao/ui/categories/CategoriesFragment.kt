package com.designbyark.layao.ui.categories

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.CategoryAdapter
import com.designbyark.layao.util.CATEGORIES_COLLECTION
import com.designbyark.layao.util.TITLE
import com.designbyark.layao.data.Category
import com.designbyark.layao.databinding.FragmentCategoriesBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CategoriesFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private var mAdapter: CategoryAdapter? = null
    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

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
        mAdapter = CategoryAdapter(options, this)

        // Assigning adapter to Recycler View
        binding.mCategoryRV.adapter = mAdapter
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
        val action = CategoriesFragmentDirections.actionNavigationCategoryToCPListFragment(categoryId)
        findNavController().navigate(action)
    }
}