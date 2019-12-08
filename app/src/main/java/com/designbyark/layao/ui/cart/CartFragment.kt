package com.designbyark.layao.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.setDiscountPrice
import com.designbyark.layao.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_cart.view.*
import java.util.*
import kotlin.math.ceil

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel

    private lateinit var grandTotal: TextView
    private lateinit var totalItems: TextView
    private lateinit var mDeleteAll: TextView

    private lateinit var mCheckout: FloatingActionButton

    private lateinit var navController: NavController

    private var totalPrice: Double = 0.0
    private var totalItemCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val count = cartViewModel.itemCount()

        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment)

        if (count > 0) {

            val root = inflater.inflate(R.layout.fragment_cart, container, false)

            val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
            if (bottomMenu.visibility == View.GONE) {
                bottomMenu.visibility = View.VISIBLE
            }

            grandTotal = root.findViewById(R.id.grand_total)
            totalItems = root.findViewById(R.id.total_items)
            mDeleteAll = root.findViewById(R.id.delete_all)
            mCheckout = root.findViewById(R.id.checkout)

            val recyclerView: RecyclerView = root.findViewById(R.id.cart_recycler_view)
            val cartAdapter = CartAdapter(requireContext(), cartViewModel)
            recyclerView.adapter = cartAdapter

            cartViewModel.allCartItems.observe(requireActivity(), Observer { items ->
                items?.let { cartAdapter.setItems(it) }
                totalItemCount = items.size
                totalItems.text = String.format(Locale.getDefault(), "x%d", totalItemCount)
            })

            cartViewModel.total.observe(requireActivity(), Observer {
                if (it == null) {
                    grandTotal.text = "Rs. 0"
                    return@Observer
                }
                grandTotal.text = String.format(Locale.getDefault(), "Rs. %.0f", it)
                totalPrice = it
            })

            mDeleteAll.setOnClickListener {
                showAlert()
            }

            mCheckout.setOnClickListener {
                if (totalPrice < 500) {
                    Toast.makeText(requireActivity(), "Amount too low, add more items", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    val args = Bundle()
                    args.putDouble("grand_total", totalPrice)
                    args.putInt("total_items", totalItemCount)
                    navController.navigate(R.id.action_navigation_cart_to_checkoutFragment, args)
                }
            }

            return root
        }

        return inflater.inflate(R.layout.fragment_empty_cart, container, false)
    }

    private fun showAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete all items")
            .setIcon(R.drawable.ic_warning_color_24dp)
            .setMessage("All items will be deleted from your cart. Do you want to proceed?")
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                cartViewModel.deleteCart()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}