package com.designbyark.layao.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_empty_cart.view.*
import java.util.*

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel

    private var totalPrice: Double = 0.0
    private var totalItemCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        if (cartViewModel.itemCount() == 0) {
            return inflater.inflate(R.layout.fragment_empty_cart, container, false)
        }

        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

        if (cartViewModel.itemCount() == 0) {
            (requireActivity() as AppCompatActivity).run {
                supportActionBar?.setTitle(R.string.title_cart)
            }

            view.mAddItemsToCart.setOnClickListener {
                Navigation.createNavigateOnClickListener(R.id.action_navigation_cart_to_navigation_home, null)
            }

            return
        }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle(R.string.cart_items)
        }

        cartViewModel.getGrandTotal().observe(requireActivity(), Observer {
            if (it == null || it < 1) {
                view.mCartTotal.text = "Rs. 0"
                totalPrice = 0.0
                return@Observer
            }
            view.mCartTotal.text = String.format(Locale.getDefault(), "Rs. %.0f", it)
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
                Navigation.createNavigateOnClickListener(R.id.action_navigation_cart_to_checkoutFragment, args)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (cartViewModel.itemCount() == 0) {
            menu.clear()
            return
        }
        menu.clear()
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cart_delete_all -> {
                if (cartViewModel.itemCount() == 0) {
                    Toast.makeText(requireContext(), "Cart is Empty!", Toast.LENGTH_LONG).show()
                    return true
                }
                showAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlert() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_all_items))
            .setIcon(R.drawable.ic_warning_color_24dp)
            .setMessage(getString(R.string.delete_all_items_desc))
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