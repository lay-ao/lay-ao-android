package com.designbyark.layao.ui.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.designbyark.layao.data.User
import com.designbyark.layao.ui.cart.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_checkout.view.*
import java.util.*

class CheckoutFragment : Fragment() {

    private var grandTotal: Double = 0.0
    private var totalItems: Int = 0

    private lateinit var navController: NavController
    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderCollection: CollectionReference
    private lateinit var userCollection: CollectionReference
    private lateinit var firebaseUser: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            grandTotal = it.getDouble("grand_total")
            totalItems = it.getInt("total_items")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val firebase = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance()
        orderCollection = firebase.collection(ORDERS_COLLECTION)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        view.mBackNav.setOnClickListener { navController.navigateUp() }

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        val blocksAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.blocks, android.R.layout.simple_spinner_dropdown_item
        )
        blocksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.mBlockSpinner.adapter = blocksAdapter

        if (firebaseUser.currentUser == null) {
            view.mRetrieveData.visibility = View.GONE
        }

        val deliveryFee = 30.0
        val totalAmount = grandTotal + deliveryFee

        view.mGrandTotal.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", grandTotal
        )
        view.mTotalItems.text = String.format(
            Locale.getDefault(),
            "%d items", totalItems
        )

        view.mDeliveryFee.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", deliveryFee
        )

        view.mTotal.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", totalAmount
        )

        view.mAddressHelp.setOnClickListener {
            showAddressWarning()
        }

        view.mPlaceOrder.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            if (firebaseUser.currentUser == null) {
                showLoginInfo()
                return@setOnClickListener
            }

            val fullName = view.mFullNameET.text.toString().trim()
            val phoneNumber = view.mContactET.text.toString().trim()
            val houseNumber = view.mHouseNumET.text.toString().trim()
            val comment = view.mCommentsET.text.toString().trim()

            if (emptyValidation(fullName, view.mFullNameIL)) return@setOnClickListener
            if (phoneValidation(phoneNumber, view.mContactIL)) return@setOnClickListener
            if (emptyValidation(houseNumber, view.mHouseNumIL)) return@setOnClickListener

            if (view.mBlockSpinner.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val order = Order()
            order.fullName = fullName.trim()
            order.contactNumber = phoneNumber.trim()
            order.houseNumber = houseNumber.trim()
            order.block = view.mBlockSpinner.selectedItemPosition
            order.completeAddress = "House #$houseNumber, ${view.mBlockSpinner.selectedItem}, Wapda Town"
            if (comment.isBlank() || comment.isEmpty()) {
                order.comment = "No comments added!"
            } else {
                order.comment = comment.trim()
            }
            order.items = cartViewModel.allCartItems.value!!
            order.orderTime = Timestamp.now()
            order.orderStatus = 0
            order.totalItems = totalItems
            order.grandTotal = grandTotal
            order.userId = firebaseUser.uid!!

            orderCollection.add(order)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Order Placed!", Toast.LENGTH_LONG).show()
                    orderCollection.document(documentReference.id)
                        .update("orderId", documentReference.id)
                    displayNotification(
                        requireContext(),
                        R.drawable.ic_favorite_red,
                        "Order received",
                        "Thank you for placing your order. Your order id is ${formatOrderId(
                            documentReference.id,
                            phoneNumber.trim()
                        )}. Kindly, contact on our helpline for any further assistance. Thank you."
                    )
                    cartViewModel.deleteCart()
                    navController.navigate(R.id.action_checkoutFragment_to_navigation_home)
                }
                .addOnFailureListener { e ->
                    Log.e(LOG_TAG, "Error adding document", e)
                }
        }

        view.mRetrieveData.setOnClickListener {
            userCollection = firebase.collection("Users")
            userCollection.document(firebaseUser.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null) {

                        if (model.fullName.isEmpty()) {
                            view.mFullNameIL.error = "No name found!"
                        } else {
                            view.mFullNameET.setText(model.fullName, TextView.BufferType.EDITABLE)
                        }

                        if (model.contact.isEmpty()) {
                            view.mContactIL.error = "No phone number found!"
                        } else {
                            view.mContactET.setText(model.contact, TextView.BufferType.EDITABLE)
                        }

                        if (model.houseNumber.isEmpty()) {
                            view.mHouseNumIL.error = "No house number found!"
                        } else {
                            view.mHouseNumET.setText(model.houseNumber, TextView.BufferType.EDITABLE)
                        }

                        if (model.blockNumber != 0) {
                            view.mBlockSpinner.setSelection(model.blockNumber)
                        }

                    }
                }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun showAddressWarning() {
        AlertDialog.Builder(requireContext())
            .setTitle("Limited Service Area")
            .setIcon(R.drawable.ic_delivery)
            .setMessage("Our delivery service is limited to Wapda Town Phase 1 & 2 only.")
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoginInfo() {
        AlertDialog.Builder(requireContext())
            .setTitle("Login")
            .setIcon(R.drawable.ic_delivery)
            .setMessage("Sign up or Login to place order.")
            .setPositiveButton("Sign Up") { _, _ ->
                navController.navigate(R.id.action_checkoutFragment_to_signUpFragment)
            }
            .setNegativeButton("Login") { _, _ ->
                navController.navigate(R.id.action_checkoutFragment_to_signInFragment)
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
