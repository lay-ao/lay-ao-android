package com.designbyark.layao.ui.checkout


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class CheckoutFragment : Fragment() {

    private var grandTotal: Double = 0.0
    private var totalItems: Int = 0

    private lateinit var navController: NavController

    private lateinit var grandTotalView: TextView
    private lateinit var totalItemsView: TextView
    private lateinit var deliveryFeeView: TextView
    private lateinit var addressHelp: TextView
    private lateinit var totalView: TextView

    private lateinit var placeOrder: Button

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

        (requireActivity() as AppCompatActivity).run{
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle("Checkout")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment)

        val root = inflater.inflate(R.layout.fragment_checkout, container, false)

        val bottomMenu: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        bottomMenu.visibility = View.GONE

        findingViews(root)

        val deliveryFee: Double = 30.0
        val totalAmount = grandTotal + deliveryFee

        grandTotalView.text = String.format(Locale.getDefault(),
            "Rs. %.0f", grandTotal)
        totalItemsView.text = String.format(Locale.getDefault(),
            "%d items", totalItems)
        deliveryFeeView.text = String.format(Locale.getDefault(),
            "Rs. %.0f", deliveryFee)
        totalView.text = String.format(Locale.getDefault(),
            "Rs. %.0f", totalAmount)

        addressHelp.setOnClickListener {
            showAddressWarning()
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

}
