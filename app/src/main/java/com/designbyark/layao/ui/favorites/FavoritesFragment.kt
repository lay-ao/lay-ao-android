package com.designbyark.layao.ui.favorites


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.data.User
import com.designbyark.layao.data.favorite.Favorite
import com.designbyark.layao.helper.MarginItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FavoritesFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var favList: List<Favorite>? = null
    private var userCollection: CollectionReference? = null

    private lateinit var favoriteCount: TextView
    private lateinit var retrieveAll: ImageButton
    private lateinit var syncAll: ImageButton

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
        syncAll = root.findViewById(R.id.sync_all)
        retrieveAll = root.findViewById(R.id.retrieve_all)

        val recyclerView: RecyclerView = root.findViewById(R.id.favorites_recycler_view)
        val favoriteAdapter = FavoriteAdapter(requireActivity(), favoriteViewModel)
        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.default_recycler_view_cell_margin).toInt()
            )
        )
        recyclerView.adapter = favoriteAdapter

        var localList: List<Favorite> = emptyList()

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        favoriteViewModel.allFavorites.observe(requireActivity(), Observer {
            it.let { favoriteAdapter.setItems(it) }
            favoriteCount.text = String.format(Locale.getDefault(), "Total Favorites: %d", it.size)
            favList = it
            localList = it
            setSyncIcon(localList)
            setDownloadIcon(it)
        })

        syncAll.setOnClickListener {
            saveToDB()
        }

        retrieveAll.setOnClickListener {
            getFavorites(localList)
        }

        return root
    }

    private fun getFavorites(localList: List<Favorite>) {
        val firestore = FirebaseFirestore.getInstance()
        if (firebaseUser != null) {
            userCollection = firestore.collection(USERS_COLLECTION)
            userCollection?.document(firebaseUser!!.uid)?.get()
                ?.addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null) {
                        if (model.favoriteItems.isNotEmpty()) {
                            for (item in model.favoriteItems) {
                                if (!localList.contains(item)) {
                                    favoriteViewModel.insert(item)
                                }
                            }
                            retrieveAll.setImageResource(R.drawable.ic_retrieve_black_24dp)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No Favorites Added!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    private fun setSyncIcon(localList: List<Favorite>) {
        val firestore = FirebaseFirestore.getInstance()
        if (firebaseUser != null) {
            userCollection = firestore.collection(USERS_COLLECTION)
            userCollection?.document(firebaseUser!!.uid)?.get()
                ?.addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null) {
                        if (model.favoriteItems.size != localList.size) {
                            syncAll.setImageResource(R.drawable.ic_sync_color_24dp)
                        }
                    }
                }
        } else {
            syncAll.visibility = View.GONE
            retrieveAll.visibility = View.GONE
        }
    }

    private fun setDownloadIcon(localList: List<Favorite>?) {
        val firestore = FirebaseFirestore.getInstance()
        if (firebaseUser != null) {
            userCollection = firestore.collection(USERS_COLLECTION)
            userCollection?.document(firebaseUser!!.uid)?.get()
                ?.addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null && localList != null) {
                        if (model.favoriteItems.size != localList.size) {
                            retrieveAll.setImageResource(R.drawable.ic_retrieve_color_24dp)
                        }
                    }
                }
        } else {
            syncAll.visibility = View.GONE
            retrieveAll.visibility = View.GONE
        }
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

    private fun saveToDB() {

        if (firebaseUser != null) {
            userCollection?.document(firebaseUser!!.uid)?.update("favoriteItems", favList)
            syncAll.setImageResource(R.drawable.ic_sync_black_24dp)
            Toast.makeText(requireContext(), "Saved on Network!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No User detected", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.favorites_menu, menu)
    }
}
