package com.designbyark.layao.ui.favorites


import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.util.MarginItemDecoration
import java.util.*

class FavoritesFragment : Fragment() {

    private lateinit var favoriteCount: TextView
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoriteViewModel = ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        favoriteCount = root.findViewById(R.id.total_favorites)

        val recyclerView: RecyclerView = root.findViewById(R.id.favorites_recycler_view)
        val favoriteAdapter = FavoriteAdapter(requireActivity(), favoriteViewModel)
        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )
        recyclerView.adapter = favoriteAdapter

        favoriteViewModel.allFavorites.observe(requireActivity(), Observer {
            it.let { favoriteAdapter.setItems(it) }
            favoriteCount.text = String.format(Locale.getDefault(), "Total Favorites: %d", it.size)
        })

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            R.id.favorites_delete -> {
                favoriteViewModel.deleteAllFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.favorites_menu, menu)
    }
}
