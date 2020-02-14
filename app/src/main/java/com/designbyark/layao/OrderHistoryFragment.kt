package com.designbyark.layao


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.designbyark.layao.common.setHorizontalListLayout
import com.designbyark.layao.data.Order
import com.designbyark.layao.ui.orders.OrderAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderHistoryFragment : Fragment(),
    OrderAdapter.OrderItemClickListener {

    private var adapter: OrderAdapter? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val firebase = FirebaseFirestore.getInstance()

        val orderCollection = firebase.collection(ORDERS_COLLECTION)

        val query = orderCollection.whereEqualTo("userId", firebaseUser!!.uid)
            .whereGreaterThanOrEqualTo("orderStatus", 5)
        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        adapter = OrderAdapter(options, requireContext(), this)

        val recyclerView: RecyclerView =
            view.findViewById(R.id.history_order_recycler_view)
        recyclerView.adapter = adapter

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        if (adapter != null) {
            adapter?.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter?.stopListening()
        }
    }

    override fun orderItemClickListener(orderId: String) {
        val args = Bundle()
        args.putString("orderId", orderId)
        navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment, args)
    }


}
