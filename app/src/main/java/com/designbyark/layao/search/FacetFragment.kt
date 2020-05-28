package com.designbyark.layao.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.designbyark.layao.R
import com.designbyark.layao.databinding.FragmentFacetBinding
import kotlinx.android.synthetic.main.fragment_facet.*
import kotlinx.android.synthetic.main.fragment_facet.view.*

class FacetFragment : Fragment() {

    private lateinit var binding: FragmentFacetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_facet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val viewModel = ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)

        binding.facetList.let {
            it.adapter = viewModel.adapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterFacet)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
