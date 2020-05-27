package com.designbyark.layao.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.designbyark.layao.R
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private val connection = ConnectionHandler()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment
        )
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        viewModel.products.observe(
            viewLifecycleOwner,
            Observer { hits -> viewModel.adapterProduct.submitList(hits) }
        )

        val searchBoxView = SearchBoxViewAppCompat(searchView)

        connection += viewModel.searchBox.connectView(searchBoxView)

        productList.let {
            it.itemAnimator = null
            it.adapter = viewModel.adapterProduct
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterProduct)
        }
        filters.setOnClickListener {
            navController.navigate(R.id.action_navigation_search_to_facetFragment)
        }

    }
}