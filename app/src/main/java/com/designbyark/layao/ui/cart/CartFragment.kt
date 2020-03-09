package com.designbyark.layao.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_cart.view.*

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel

    private lateinit var navController: NavController

    private var totalPrice: Double = 0.0
    private var totalItemCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }


        cartViewModel.getGrandTotal().observe(requireActivity(), Observer {
            if (it == null) {
                view.mGrandTotal.text = "Rs. 0"
                return@Observer
            }
            view.mGrandTotal.text = String.format(Locale.getDefault(), "Rs. %.0f", it)
            totalPrice = it
        })

        val cartAdapter =
            CartAdapter(requireContext(), cartViewModel)
        view.mCartRV.adapter = cartAdapter

        cartViewModel.allCartItems.observe(requireActivity(), Observer { items ->
            items?.let { cartAdapter.setItems(it) }
            totalItemCount = items.size
            view.mTotalItems.text = String.format(Locale.getDefault(), "x%d", totalItemCount)
        })

        view.mDeleteAll.setOnClickListener {
            showAlert()
        }

        view.mCheckout.setOnClickListener {
            if (totalPrice < 500) {
                Toast.makeText(
                    requireActivity(),
                    "Amount too low, add more items",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            } else {
                val args = Bundle()
                args.putDouble("grand_total", totalPrice)
                args.putInt("total_items", totalItemCount)
                navController.navigate(R.id.action_navigation_cart_to_checkoutFragment, args)
            }
        }
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