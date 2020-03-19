package com.designbyark.layao.ui.orders


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_no_auth_order.view.*
import kotlinx.android.synthetic.main.fragment_orders.view.*

class OrdersFragment : Fragment(), OrderAdapter.OrderItemClickListener {

    private var firebaseUser: FirebaseUser? = null
    private var adapter: OrderAdapter? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            return inflater.inflate(R.layout.fragment_no_auth_order, container, false)
        }
        return inflater.inflate(R.layout.fragment_orders, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        if (firebaseUser == null) {
            view.mLoginAuth.setOnClickListener {
                navController.navigate(R.id.action_navigation_orders_to_signInFragment)
            }
            view.mRegisterAuth.setOnClickListener {
                navController.navigate(R.id.action_navigation_orders_to_signUpFragment)
            }
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val orderCollection = firestore.collection(ORDERS_COLLECTION)

        val query = orderCollection.whereEqualTo("userId", firebaseUser?.uid)
            .whereLessThanOrEqualTo("orderStatus", 4)
            .orderBy("orderStatus", Query.Direction.ASCENDING)

        query.addSnapshotListener { value, e ->
            var count = 0
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            value?.forEach { _ ->
                count++
            }

            if (count > 0) {
                view.mNoOrdersLayout.visibility = View.GONE
            } else {
                view.mNoOrdersLayout.visibility = View.VISIBLE
                view.mBuySomething.setOnClickListener {
                    navController.navigate(R.id.action_navigation_orders_to_navigation_home)
                }
            }
        }

        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        adapter = OrderAdapter(options, this)
        view.mActiveOrdersRV.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    override fun orderItemClickListener(orderId: String) {
        val args = Bundle()
        args.putString("orderId", orderId)
        navController.navigate(R.id.action_navigation_orders_to_orderDetailFragment, args)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.orders_history -> {
                navController.navigate(R.id.action_navigation_orders_to_orderHistoryFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (firebaseUser == null) {
            menu.clear()
        } else {
            menu.clear()
            inflater.inflate(R.menu.orders_menu, menu)
        }
    }

}
