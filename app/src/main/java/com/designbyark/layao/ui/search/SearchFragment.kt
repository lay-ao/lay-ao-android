package com.designbyark.layao.ui.search

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.designbyark.layao.R
import com.designbyark.layao.databinding.FragmentSearchBinding
import com.designbyark.layao.util.isConnectedToInternet
import com.designbyark.layao.viewmodels.SearchViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private val connection = ConnectionHandler()
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.search = this
        return binding.root
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

        binding.productList.let {
            it.itemAnimator = null
            it.adapter = viewModel.adapterProduct
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterProduct)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.general_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        requireActivity().invalidateOptionsMenu()
        if (!isConnectedToInternet(requireContext())) {
            menu.findItem(R.id.no_wifi).isVisible = true
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.no_wifi -> {
                showNoInternetDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage("Search requires network connection, kindly connect to a " +
                    "network and try searching again")
            .setPositiveButton("Try Again") { dialog, _ ->
                if (isConnectedToInternet(requireContext())) {
                    dialog.dismiss()
                } else {
                    showNoInternetDialog()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun displayFilters() {
        findNavController().navigate(R.id.action_navigation_search_to_facetFragment)
    }

}
