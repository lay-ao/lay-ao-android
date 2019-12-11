package com.designbyark.layao


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class OrderDetailFragment : Fragment() {

    private var orderId: String? = null
    private var orderDocument: DocumentReference? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            orderId = it.getString("orderId")
        }

        val firebase = FirebaseFirestore.getInstance()
        val collectionReference = firebase.collection(ORDERS_COLLECTION)
        orderDocument = orderId?.let { collectionReference.document(it) }
        val title = orderId?.take(5)?.toUpperCase(Locale.getDefault())

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle("Order #$title")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        orderDocument?.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(LOG_TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d(LOG_TAG, "Current data: null")
            }
        }

        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }
}
