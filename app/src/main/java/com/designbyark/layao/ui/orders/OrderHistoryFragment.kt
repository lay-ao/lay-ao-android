package com.designbyark.layao.ui.orders


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.ORDERS_COLLECTION
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_order_history.view.*

class OrderHistoryFragment : Fragment(),
    OrderAdapter.OrderItemClickListener {

    private var adapter: OrderAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
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

        adapter = OrderAdapter(options, this)

        view.mHistoryOrdersRV.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
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
        Navigation.createNavigateOnClickListener(
            R.id.action_orderHistoryFragment_to_orderDetailFragment,
            args
        )
    }


}
