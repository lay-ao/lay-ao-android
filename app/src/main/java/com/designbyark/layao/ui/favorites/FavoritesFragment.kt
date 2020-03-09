package com.designbyark.layao.ui.favorites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.USERS_COLLECTION
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var favoritesCollection: CollectionReference

    private var firebaseUser: FirebaseUser? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        if (firebaseUser == null) {

            val view = inflater.inflate(R.layout.fragment_no_favorites, container, false)

            val userAuthIntent = view.findViewById<TextView>(R.id.mLoginAuth)
            userAuthIntent.setOnClickListener {
                navController.navigate(R.id.action_favoritesFragment_to_navigation_user)
            }

            return view
        }

        favoritesCollection = firestore.collection(USERS_COLLECTION).document(firebaseAuth.uid!!)
            .collection("Favorites")

        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseUser == null) return

        val backNav = view.findViewById<ImageButton>(R.id.mBackNav)
        backNav.setOnClickListener { navController.navigateUp() }

        val options = FirestoreRecyclerOptions.Builder<Favorites>()
            .setQuery(favoritesCollection, Favorites::class.java)
            .build()

        val recyclerView: RecyclerView = view.findViewById(R.id.favorites_recycler_view)
        favoriteAdapter = FavoriteAdapter(options, favoritesCollection)
        recyclerView.adapter = favoriteAdapter
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
