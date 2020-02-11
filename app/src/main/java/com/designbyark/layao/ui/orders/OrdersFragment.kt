package com.designbyark.layao.ui.orders


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.designbyark.layao.common.setHorizontalListLayout
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrdersFragment : Fragment(), OrderAdapter.OrderItemClickListener {

    private var orderAdapter: OrderAdapter? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebase = FirebaseFirestore.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        if (firebaseUser == null) {

            val emptyOrderView = inflater.inflate(R.layout.fragment_no_order, container, false)

            val userNav: TextView = emptyOrderView.findViewById(R.id.login_sign_up_button)

            userNav.setOnClickListener {
                navController.navigate(R.id.action_navigation_orders_to_navigation_user)
            }

            return emptyOrderView
        }

        val orderCollection = firebase.collection(ORDERS_COLLECTION)

        val query = orderCollection.whereEqualTo("userId", firebaseUser.uid)
        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        orderAdapter = OrderAdapter(options, requireContext(), orderCollection, this)

        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.active_order_recycler_view)
        setHorizontalListLayout(recyclerView, requireContext())
        recyclerView.adapter = orderAdapter

        return root

    }

    override fun onStart() {
        super.onStart()
        if (orderAdapter != null) {
            orderAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (orderAdapter != null) {
            orderAdapter!!.stopListening()
        }
    }

    override fun orderItemClickListener(orderId: String) {
        val args = Bundle()
        args.putString("orderId", orderId)
        navController.navigate(R.id.action_navigation_orders_to_orderDetailFragment, args)
    }

}
