package com.designbyark.layao.ui.orders


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.designbyark.layao.common.setListLayout
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class OrdersFragment : Fragment(), OrderAdapter.OrderItemClickListener {

    private lateinit var orderAdapter: OrderAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebase = FirebaseFirestore.getInstance()
        val orderCollection = firebase.collection(ORDERS_COLLECTION)
        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment)

        val query = orderCollection.whereLessThanOrEqualTo("orderStatus", 5)
            .orderBy("orderStatus")
        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        orderAdapter = OrderAdapter(options, requireContext(), orderCollection, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.active_order_recycler_view)
        setListLayout(recyclerView, requireContext())
        recyclerView.adapter = orderAdapter

        return root
    }

    override fun onStart() {
        super.onStart()
        orderAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        orderAdapter.startListening()
    }

    override fun orderItemClickListener(orderId: String) {
        val args = Bundle()
        args.putString("orderId", orderId)
        navController.navigate(R.id.action_navigation_orders_to_orderDetailFragment, args)
    }

}
