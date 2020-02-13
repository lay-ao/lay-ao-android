package com.designbyark.layao.ui.checkout


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CheckoutFragment : Fragment() {

    private var grandTotal: Double = 0.0
    private var totalItems: Int = 0

    private lateinit var navController: NavController
    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderCollection: CollectionReference
    private lateinit var userCollection: CollectionReference
    private lateinit var firebaseUser: FirebaseAuth

    private lateinit var grandTotalView: TextView
    private lateinit var totalItemsView: TextView
    private lateinit var deliveryFeeView: TextView
    private lateinit var addressHelp: TextView
    private lateinit var totalView: TextView

    private lateinit var placeOrder: Button
    private lateinit var retrieveData: Button

    private lateinit var blocks: Spinner

    private lateinit var fullNameInputLayout: TextInputLayout
    private lateinit var fullNameEditText: TextInputEditText

    private lateinit var phoneNumberInputLayout: TextInputLayout
    private lateinit var phoneNumberEditText: TextInputEditText

    private lateinit var houseNoInputLayout: TextInputLayout
    private lateinit var houseNoEditText: TextInputEditText

    private lateinit var commentInputLayout: TextInputLayout
    private lateinit var commentEditText: TextInputEditText


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

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val firebase = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance()
        orderCollection = firebase.collection("Orders")

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle("Checkout")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val root = inflater.inflate(R.layout.fragment_checkout, container, false)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        findingViews(root)

        val blocksAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.blocks, android.R.layout.simple_spinner_dropdown_item
        )
        blocksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blocks.adapter = blocksAdapter

        if (firebaseUser.currentUser == null) {
            retrieveData.visibility = View.GONE
        }

        val deliveryFee = 30.0
        val totalAmount = grandTotal + deliveryFee

        grandTotalView.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", grandTotal
        )
        totalItemsView.text = String.format(
            Locale.getDefault(),
            "%d items", totalItems
        )
        deliveryFeeView.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", deliveryFee
        )
        totalView.text = String.format(
            Locale.getDefault(),
            "Rs. %.0f", totalAmount
        )

        addressHelp.setOnClickListener {
            showAddressWarning()
        }

        placeOrder.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            if (firebaseUser.currentUser == null) {
                showLoginInfo()
                return@setOnClickListener
            }

            val fullName = fullNameEditText.text.toString().trim()
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val houseNumber = houseNoEditText.text.toString().trim()
            val comment = commentEditText.text.toString().trim()

            if (emptyValidation(fullName, fullNameInputLayout)) return@setOnClickListener
            if (phoneValidation(phoneNumber, phoneNumberInputLayout)) return@setOnClickListener
            if (emptyValidation(houseNumber, houseNoInputLayout)) return@setOnClickListener

            if (blocks.selectedItemPosition == 0) {
                Toast.makeText(requireContext(), "Invalid block selected", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val order = Order()
            order.fullName = fullName.trim()
            order.contactNumber = phoneNumber.trim()
            order.houseNumber = houseNumber.trim()
            order.block = blocks.selectedItemPosition
            order.completeAddress = "House #$houseNumber, ${blocks.selectedItem}, Wapda Town"
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
                        R.drawable.ic_favorite_color_24dp,
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


        retrieveData.setOnClickListener {
            userCollection = firebase.collection("Users")
            userCollection.document(firebaseUser.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null) {

                        if (model.fullName.isEmpty()) {
                            fullNameInputLayout.error = "No name found!"
                        } else {
                            fullNameEditText.setText(model.fullName, TextView.BufferType.EDITABLE)
                        }

                        if (model.contact.isEmpty()) {
                            phoneNumberInputLayout.error = "No phone number found!"
                        } else {
                            phoneNumberEditText.setText(model.contact, TextView.BufferType.EDITABLE)
                        }

                        if (model.houseNumber.isEmpty()) {
                            houseNoInputLayout.error = "No house number found!"
                        } else {
                            houseNoEditText.setText(model.houseNumber, TextView.BufferType.EDITABLE)
                        }

                        if (model.blockNumber != 0) {
                            blocks.setSelection(model.blockNumber)
                        }

                    }
                }
        }

        return root
    }


    private fun findingViews(root: View) {
        grandTotalView = root.findViewById(R.id.grand_total)
        totalItemsView = root.findViewById(R.id.total_items)
        deliveryFeeView = root.findViewById(R.id.delivery_fee)
        addressHelp = root.findViewById(R.id.address_help)
        totalView = root.findViewById(R.id.total)
        placeOrder = root.findViewById(R.id.place_order)
        retrieveData = root.findViewById(R.id.retrieve_data)
        blocks = root.findViewById(R.id.block_spinner)
        fullNameInputLayout = root.findViewById(R.id.full_name_input_layout)
        fullNameEditText = root.findViewById(R.id.full_name_edit_text)
        phoneNumberInputLayout = root.findViewById(R.id.phone_number_input_layout)
        phoneNumberEditText = root.findViewById(R.id.phone_number_edit_text)
        houseNoInputLayout = root.findViewById(R.id.house_no_input_layout)
        houseNoEditText = root.findViewById(R.id.house_no_edit_text)
        commentInputLayout = root.findViewById(R.id.comments_text_input_layout)
        commentEditText = root.findViewById(R.id.comments_text_edit_text)
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
            .setPositiveButton("Sign Up") { dialog, _ ->
                navController.navigate(R.id.action_checkoutFragment_to_signUpFragment)
            }
            .setNegativeButton("Login") { dialog, _ ->
                navController.navigate(R.id.action_checkoutFragment_to_signInFragment)
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
