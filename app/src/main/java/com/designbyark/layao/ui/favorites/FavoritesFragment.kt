package com.designbyark.layao.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.FavoriteAdapter
import com.designbyark.layao.databinding.FragmentFavoritesBinding
import com.designbyark.layao.util.MarginItemDecoration
import com.designbyark.layao.viewmodels.FavoritesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoriteViewModel: FavoritesViewModel

    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        favoriteViewModel = ViewModelProvider(requireActivity()).get(FavoritesViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        binding.favorite = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }


        favoriteAdapter = FavoriteAdapter(favoriteViewModel)

        favoriteViewModel.allFavorites.observe(requireActivity(), Observer {
            favoriteAdapter?.setFavorites(it)
            if (it.isEmpty()) {
                binding.noFavoritesSection.visibility = View.VISIBLE
            } else {
                binding.noFavoritesSection.visibility = View.GONE
            }
        })

        binding.mFavoritesRV.let {
            it.adapter = favoriteAdapter
            it.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
                )
            )
        }
    }

    fun login() {
        findNavController().navigate(R.id.action_favoritesFragment_to_signInFragment)
    }

    fun register() {
        findNavController().navigate(R.id.action_favoritesFragment_to_signUpFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
