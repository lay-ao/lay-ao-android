package com.designbyark.layao.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R

class CartFragment : Fragment() {



    private lateinit var cartViewModel: CartViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val count = cartViewModel.itemCount()

        if (count > 0) {

            val root = inflater.inflate(R.layout.fragment_cart, container, false)

            val recyclerView: RecyclerView = root.findViewById(R.id.cart_recycler_view)
            val cartAdapter = CartAdapter(requireContext(), cartViewModel)
            recyclerView.adapter = cartAdapter

            cartViewModel.allCartItems.observe(requireActivity(), Observer { items ->
                items?.let { cartAdapter.setItems(it) }
            })
            return root
        }

        return inflater.inflate(R.layout.fragment_empty_cart, container, false)
    }
}