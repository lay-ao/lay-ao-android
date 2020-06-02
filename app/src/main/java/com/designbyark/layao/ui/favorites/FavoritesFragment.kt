package com.designbyark.layao.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.FavoriteAdapter
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.FragmentFavoritesBinding
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.util.MarginItemDecoration
import com.designbyark.layao.util.PRODUCTS_COLLECTION
import com.designbyark.layao.viewmodels.FavoritesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment(), FavoriteAdapter.OnFavoriteClickListener {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoriteViewModel: FavoritesViewModel

    private var favoriteAdapter: FavoriteAdapter? = null

    private var listSize: Int = -1

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


        favoriteAdapter = FavoriteAdapter(favoriteViewModel, this)

        favoriteViewModel.allFavorites.observe(requireActivity(), Observer { favorites ->
            favoriteAdapter?.setFavorites(favorites)
            listSize = favorites.size
            if (favorites.isEmpty()) {
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
        inflater.inflate(R.menu.favorites_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_fav_delete -> {
                showAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        requireActivity().invalidateOptionsMenu()
        menu.findItem(R.id.action_fav_delete).isVisible = listSize != 0

        super.onPrepareOptionsMenu(menu)
    }


    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete all Favorites")
            .setMessage("Are you sure you want to delete all your favorite items")
            .setPositiveButton("Yes") { dialog, _ ->
                favoriteViewModel.deleteAllFavorites()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onFavItemClickListener(productId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)
        val document = collection.document(productId)
        document.get()
            .addOnSuccessListener { documentSnapshot ->
                val product = documentSnapshot.toObject(Products::class.java)
                if (product != null) {
                    val action =
                        FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                            product
                        )
                    findNavController().navigate(action)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(LOG_TAG, "get failed with ", exception)
            }
    }

}
