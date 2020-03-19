package com.designbyark.layao.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.designbyark.layao.R
import kotlinx.android.synthetic.main.fragment_facet.*
import kotlinx.android.synthetic.main.fragment_facet.view.*

class FacetFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        view.facetList.let {
            it.adapter = viewModel.adapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterFacet)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

}
