package com.designbyark.layao.ui.favorites


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.adapters.FavoriteAdapter
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.util.MarginItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import kotlinx.android.synthetic.main.fragment_no_favorites.view.*

class FavoritesFragment : Fragment() {

    private lateinit var favoritesCollection: CollectionReference

    private var firebaseUser: FirebaseUser? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            return inflater.inflate(R.layout.fragment_no_favorites, container, false)
        }

        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
//            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            supportActionBar?.setSubtitle("Prices may subject to change.")
//        }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

        if (firebaseUser == null) {
            view.mLoginAuth.setOnClickListener {
                Navigation.createNavigateOnClickListener(R.id.action_favoritesFragment_to_signInFragment)
            }

            view.mRegisterAuth.setOnClickListener {
                Navigation.createNavigateOnClickListener(R.id.action_favoritesFragment_to_signUpFragment)
            }

            return
        }

        val firestore = FirebaseFirestore.getInstance()
        favoritesCollection = firestore.collection(USERS_COLLECTION).document(firebaseUser?.uid!!)
            .collection("Favorites")

        val options = FirestoreRecyclerOptions.Builder<Favorites>()
            .setQuery(favoritesCollection, Favorites::class.java)
            .build()

        favoriteAdapter = FavoriteAdapter(
            options,
            favoritesCollection
        )
        view.mFavoritesRV.let {
            it.adapter = favoriteAdapter
            it.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt(), 2
                )
            )
        }
    }

    override fun onDestroyView() {
//        (requireActivity() as AppCompatActivity).run {
//            supportActionBar?.setSubtitle(null)
//        }
        super.onDestroyView()
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
