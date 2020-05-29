package com.designbyark.layao.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.FavoriteAdapter
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.databinding.FragmentFavoritesBinding
import com.designbyark.layao.databinding.FragmentNoFavoritesBinding
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var favoritesCollection: CollectionReference
    private lateinit var emptyLayout: FragmentNoFavoritesBinding
    private lateinit var binding: FragmentFavoritesBinding

    private var firebaseUser: FirebaseUser? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            emptyLayout =
                DataBindingUtil.inflate(inflater, R.layout.fragment_no_favorites, container, false)
            emptyLayout.fav = this
            return emptyLayout.root
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
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

        if (firebaseUser == null) {
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        favoritesCollection = firestore.collection(USERS_COLLECTION).document(firebaseUser?.uid!!)
            .collection("Favorites")

        val options = FirestoreRecyclerOptions.Builder<Favorites>()
            .setQuery(favoritesCollection, Favorites::class.java)
            .build()

        favoriteAdapter = FavoriteAdapter(options, favoritesCollection)

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

    override fun onStart() {
        super.onStart()
        if (favoriteAdapter != null)
            favoriteAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (favoriteAdapter != null)
            favoriteAdapter?.stopListening()
    }
}
