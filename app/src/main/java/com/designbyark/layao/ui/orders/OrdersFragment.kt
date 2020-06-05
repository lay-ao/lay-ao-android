package com.designbyark.layao.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.OrderAdapter
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.util.ORDERS_COLLECTION
import com.designbyark.layao.data.Order
import com.designbyark.layao.databinding.FragmentNoAuthOrderBinding
import com.designbyark.layao.databinding.FragmentOrdersBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class OrdersFragment : Fragment(), OrderAdapter.OrderItemClickListener {

    private var firebaseUser: FirebaseUser? = null
    private var adapter: OrderAdapter? = null

    private lateinit var emptyBinding: FragmentNoAuthOrderBinding
    private lateinit var binding: FragmentOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            emptyBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_no_auth_order, container, false)
            emptyBinding.auth = this
            return emptyBinding.root
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)
        binding.order = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        if (firebaseUser == null) {
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val orderCollection = firestore.collection(ORDERS_COLLECTION)

        val query = orderCollection.whereEqualTo("userId", firebaseUser?.uid)
            .whereLessThanOrEqualTo("orderStatus", 4)
            // .orderBy("orderStatus", Query.Direction.DESCENDING)

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
                binding.mNoOrdersLayout.visibility = View.GONE
            } else {
                binding.mNoOrdersLayout.visibility = View.VISIBLE
            }
        }

        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        adapter = OrderAdapter(options, this)
        binding.mActiveOrdersRV.adapter = adapter

    }

    fun buySomething() {
        findNavController().navigate(R.id.action_navigation_orders_to_navigation_home)
    }

    fun login() {
        findNavController().navigate(R.id.action_navigation_orders_to_signInFragment)
    }

    fun register() {
        findNavController().navigate(R.id.action_navigation_orders_to_signUpFragment)
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

    override fun orderItemClickListener(order: Order) {
        val action = OrdersFragmentDirections.actionNavigationOrdersToOrderDetailFragment(order)
        findNavController().navigate(action)
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
