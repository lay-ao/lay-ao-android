package com.designbyark.layao.ui.checkout

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var auth: FirebaseAuth

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.setHomeButtonEnabled(true)
        }
        setHasOptionsMenu(true)


        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        orderCollection = firebase.collection(ORDERS_COLLECTION)
        userCollection = firebase.collection("Users")

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        if (auth.currentUser == null) {
            view.mRetrieveData.visibility = View.GONE
        }

        val deliveryFee = 30.0
        var totalAmount: Double?

        userCollection.document(auth.currentUser?.uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val fineCount = documentSnapshot.getLong("fineCount") ?: 0
                    Log.d(LOG_TAG, fineCount.toString())
                    when {
                        fineCount in 1..2 -> {
                            Log.d(LOG_TAG, "fineCount > 0")
                            Log.d(LOG_TAG, "fineCount -> $fineCount")
                            totalAmount = addFine(view, fineCount)
                            view.mGrandTotalAmount.text = String.format(
                                Locale.getDefault(),
                                "Rs. %.0f", totalAmount
                            )
                        }
                        fineCount >= 3 -> {
                            Log.d(LOG_TAG, "fineCount >= 3")
                            Log.d(LOG_TAG, "fineCount -> $fineCount")
                            totalAmount = addFine(view, fineCount)
                            view.mPlaceOrder.visibility = View.INVISIBLE
                            view.mFineCountDesc.visibility = View.VISIBLE
                            view.mGrandTotalAmount.text = String.format(
                                Locale.getDefault(),
                                "Rs. %.0f", totalAmount
                            )
                        }
                        else -> {
                            Log.d(LOG_TAG, "fineCount -> else")
                            Log.d(LOG_TAG, "fineCount -> $fineCount")
                            view.mFineCountDesc.visibility = View.GONE
                            view.mPlaceOrder.visibility = View.VISIBLE
                            view.mFineCountLabel.visibility = View.INVISIBLE
                            view.mFineCount.visibility = View.INVISIBLE
                            totalAmount = grandTotal + deliveryFee
                            view.mGrandTotalAmount.text = String.format(
                                Locale.getDefault(),
                                "Rs. %.0f", totalAmount
                            )
                        }
                    }
                }
            }

        view.mCartTotal.text = String.format(
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

        view.mGoogleMaps.setOnClickListener {
            if (isLocationPermissionAvailable(requireActivity())) {
                if (isGPSEnabled(requireContext())) {
                    navController.navigate(R.id.action_checkoutFragment_to_mapFragment)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setMessage("To continue, turn on device location, which Google's location service")
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                        .setNegativeButton(android.R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create().show()
                }
            }
        }

        view.mPlaceOrder.setOnClickListener {

            if (!isConnectedToInternet(requireContext())) {
                Log.e(LOG_TAG, "Not connected to the internet!")
                return@setOnClickListener
            }

            if (auth.currentUser == null) {
                showLoginInfo()
                return@setOnClickListener
            }

            val fullName = view.mFullNameET.text.toString().trim()
            val phoneNumber = view.mContactET.text.toString().trim()
            val address = view.mAddressET.text.toString().trim()
            val comment = view.mCommentsET.text.toString().trim()

            if (emptyValidation(fullName, view.mFullNameIL)) return@setOnClickListener
            if (phoneValidation(phoneNumber, view.mContactIL)) return@setOnClickListener
            if (emptyValidation(address, view.mAddressIL)) return@setOnClickListener

            val order = Order()
            order.fullName = fullName.trim()
            order.contactNumber = phoneNumber.trim()
            order.completeAddress = address
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
            order.userId = auth.currentUser?.uid!!
            order.cancelled = false

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
            userCollection.document(auth.currentUser!!.uid).get()
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

                        if (model.completeAddress.isEmpty()) {
                            view.mAddressIL.error = "No Address found!"
                        } else {
                            view.mAddressET.setText(model.houseNumber, TextView.BufferType.EDITABLE)
                        }

                    }
                }
        }
    }

    private fun addFine(view: View, fineCount: Long): Double {
        view.mFineCountLabel.visibility = View.VISIBLE
        view.mFineCount.visibility = View.VISIBLE
        view.mFineCountLabel.text = String.format(Locale.getDefault(), "FINE (x%d)", fineCount)
        view.mFineCount.text = String.format(Locale.getDefault(), "Rs. %.2f", (30.0 * fineCount))
        return grandTotal + 30.0 + (30.0 * fineCount)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navController.navigate(R.id.action_checkoutFragment_to_mapFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You did not give permissions to get location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
