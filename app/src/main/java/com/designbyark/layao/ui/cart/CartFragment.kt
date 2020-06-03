package com.designbyark.layao.ui.cart

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.designbyark.layao.R
import com.designbyark.layao.adapters.CartAdapter
import com.designbyark.layao.data.Checkout
import com.designbyark.layao.databinding.FragmentCartBinding
import com.designbyark.layao.viewmodels.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var binding: FragmentCartBinding

    private var totalPrice: Double = 0.0
    private var totalItemCount: Int = 0

    private var listSize: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        binding.cart = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        if (bottomMenu.visibility == View.GONE) {
            bottomMenu.visibility = View.VISIBLE
        }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setTitle(R.string.title_cart)
        }

        cartViewModel.getGrandTotal().observe(requireActivity(), Observer {
            if (it == null || it < 1) {
                totalPrice = 0.0
                binding.emptyCartSection.visibility = View.VISIBLE
                binding.statSection.visibility = View.GONE
                return@Observer
            } else {
                binding.emptyCartSection.visibility = View.GONE
                binding.statSection.visibility = View.VISIBLE
                binding.mCartTotal.text = String.format(Locale.getDefault(), "Rs. %.0f", it)
                totalPrice = it
            }
        })

        val cartAdapter = CartAdapter(cartViewModel)
        binding.mCartRV.adapter = cartAdapter

        cartViewModel.allCartItems.observe(requireActivity(), Observer { items ->
            listSize = items.size
            items?.let { cartAdapter.setItems(it) }
            totalItemCount = items.size
            binding.mTotalItems.text = String.format(Locale.getDefault(), "x%d", totalItemCount)
        })
    }

    fun moveToHome() {
        findNavController().navigate(R.id.action_navigation_cart_to_navigation_home)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.cart_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        requireActivity().invalidateOptionsMenu()
        menu.findItem(R.id.cart_delete_all).isVisible = listSize != 0

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

    fun checkout() {
        val checkout = Checkout(totalPrice, totalItemCount)
        val action = CartFragmentDirections.actionNavigationCartToCheckoutFragment(checkout)
        findNavController().navigate(action)
    }
}