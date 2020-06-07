package com.designbyark.layao.ui.checkout

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.data.Order
import com.designbyark.layao.data.User
import com.designbyark.layao.databinding.FragmentCheckoutBinding
import com.designbyark.layao.util.*
import com.designbyark.layao.viewmodels.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class CheckoutFragment : Fragment() {


    private val args: CheckoutFragmentArgs by navArgs()

    private var openingTimeString: String? = ""
    private lateinit var closingTime: LocalTime
    private lateinit var openingTime: LocalTime
    private lateinit var binding: FragmentCheckoutBinding

    private var totalAmount: Double = 0.0

    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderCollection: CollectionReference
    private lateinit var userCollection: CollectionReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        binding.checkout = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        orderCollection = firebase.collection(ORDERS_COLLECTION)
        userCollection = firebase.collection("Users")

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomMenu.visibility = View.GONE

        var deliveryFee = 0.0
        if (args.checkout.cartTotal < 1000.0) {
            deliveryFee = 50.0
            binding.deliveryChargesMsg.visibility = View.VISIBLE
        }

        var itemPlurals = "item"
        if (args.checkout.totalItems > 1) {
            itemPlurals = "items"
        }

        totalAmount = args.checkout.cartTotal + deliveryFee

        if (auth.currentUser == null) {
            binding.mRetrieveData.visibility = View.GONE
        }

        binding.mCartTotal.text =
            String.format(Locale.getDefault(), "Rs. %.0f", args.checkout.cartTotal)
        binding.mTotalItems.text =
            String.format(Locale.getDefault(), "%d %s", args.checkout.totalItems, itemPlurals)
        binding.mDeliveryFee.text = String.format(Locale.getDefault(), "Rs. %.0f", deliveryFee)
        binding.mGrandTotalAmount.text = String.format(Locale.getDefault(), "Rs. %.0f", totalAmount)

        firebase.collection("Misc").document("store-timing")
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    Log.d(LOG_TAG, exception.localizedMessage, exception)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.d(LOG_TAG, "Misc: snapshot is null!")
                    return@addSnapshotListener
                }

                openingTimeString = snapshot.getString("opening")
                val closingTimeString = snapshot.getString("closing")

                openingTime =
                    LocalTime.parse(openingTimeString, DateTimeFormat.forPattern("hh:mm a"))
                closingTime =
                    LocalTime.parse(closingTimeString, DateTimeFormat.forPattern("hh:mm a"))

                binding.serviceHours.text =
                    String.format("Service Hours: %s - %s", openingTimeString, closingTimeString)

            }

        if (auth.currentUser != null) {
            userCollection.document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val model = it.toObject(User::class.java)
                    if (model != null) {

                        // Set name
                        binding.mFullNameET.setText(model.fullName, TextView.BufferType.EDITABLE)

                        // Set contact
                        binding.mContactET.setText(model.contact, TextView.BufferType.EDITABLE)

                        // Set house number
                        binding.mHouseNumberET.setText(
                            model.houseNumber,
                            TextView.BufferType.EDITABLE
                        )

                        // Set block number
                        binding.blockNumber.setSelection(model.blockNumber)

                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    fun retrieveInfo() {
        userCollection.document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val model = it.toObject(User::class.java)
                if (model != null) {

                    // Set name
                    if (model.fullName.isEmpty()) {
                        binding.mFullNameIL.error = "No Name found!"
                    } else {
                        binding.mFullNameET.setText(model.fullName, TextView.BufferType.EDITABLE)
                    }

                    if (model.contact.isEmpty()) {
                        binding.mContactIL.error = "No Contact entered!"
                    } else {
                        binding.mContactET.setText(model.contact, TextView.BufferType.EDITABLE)
                    }

                    if (model.houseNumber.isEmpty()) {
                        binding.mHouseNumberIL.error = "No House No. found"
                    } else {
                        binding.mHouseNumberET.setText(
                            model.houseNumber,
                            TextView.BufferType.EDITABLE
                        )
                    }

                    if (model.blockNumber == 0) {
                        Toast.makeText(requireContext(), "No Block found", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        binding.blockNumber.setSelection(model.blockNumber)
                    }

                }
            }
    }

    fun placeOrder() {
        if (!isConnectedToInternet(requireContext())) {
            Toast.makeText(requireContext(), "Not connected to the internet!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (auth.currentUser == null) {
            showLoginInfo()
            return
        }

        val fullName = binding.mFullNameET.text.toString().trim()
        val phoneNumber = binding.mContactET.text.toString().trim()
        val houseNumber = binding.mHouseNumberET.text.toString().trim()

        if (emptyValidation(fullName, binding.mFullNameIL)) return
        if (phoneValidation(phoneNumber, binding.mContactIL)) return
        if (emptyValidation(houseNumber, binding.mHouseNumberIL)) return

        if (binding.blockNumber.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "No block selected", Toast.LENGTH_SHORT).show()
            return
        }

        val completeAddress =
            "House #$houseNumber, ${binding.blockNumber.selectedItem}, Wapda Town, Lahore"

        val order = Order()
        order.fullName = fullName.trim()
        order.contactNumber = phoneNumber.trim()
        order.completeAddress = completeAddress
        order.block = binding.blockNumber.selectedItemPosition
        order.items = cartViewModel.allCartItems.value!!
        order.orderTime = Timestamp.now()
        order.totalItems = args.checkout.totalItems
        order.grandTotal = totalAmount
        order.userId = auth.currentUser?.uid!!
        order.cancelled = false

        val now = LocalTime.now()
        if (now > closingTime) {
            displayScheduledOrderDialog(order, phoneNumber)
        } else if (now > openingTime) {
            displayConfirmationDialog(order, phoneNumber)
        }
    }

    private fun displayScheduledOrderDialog(order: Order, phoneNumber: String) {

        val tomorrow = LocalDateTime.now().plusDays(1)
            .withTime(9, 0, 0, 0)

        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_time)
            .setTitle("Scheduling Order")
            .setMessage(
                "Your order will be scheduled for tomorrow (${formatDate(
                    tomorrow.toDate()
                )} at ${formatTime(tomorrow.toDate())}). " +
                        "Are you sure you want to continue?"
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                order.scheduledTime = Timestamp(tomorrow.toDate())
                order.scheduled = true
                order.orderStatus = -1
                placeOrder(order, phoneNumber)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun displayConfirmationDialog(order: Order, phoneNumber: String) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Delivery Address")
            .setMessage("Are you sure you want us to deliver at ${order.completeAddress}")
            .setPositiveButton("Yes") { _, _ ->
                order.orderStatus = 0
                placeOrder(order, phoneNumber)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun placeOrder(order: Order, phoneNumber: String) {
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
                findNavController().navigate(R.id.action_checkoutFragment_to_navigation_home)
            }
            .addOnFailureListener { e ->
                Log.e(LOG_TAG, "Error adding document", e)
            }
    }

    private fun showLoginInfo() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Login")
            .setIcon(R.drawable.ic_delivery)
            .setMessage("Sign up or Login to place order.")
            .setPositiveButton("Sign Up") { _, _ ->
                Navigation.createNavigateOnClickListener(R.id.action_checkoutFragment_to_signUpFragment)
            }
            .setNegativeButton("Login") { _, _ ->
                Navigation.createNavigateOnClickListener(R.id.action_checkoutFragment_to_signInFragment)
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
