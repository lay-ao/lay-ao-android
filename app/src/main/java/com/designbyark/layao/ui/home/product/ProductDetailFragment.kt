package com.designbyark.layao.ui.home.product


import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.PRODUCTS_COLLECTION
import com.designbyark.layao.common.setDiscountPrice
import com.designbyark.layao.common.setQuantityPrice
import com.designbyark.layao.data.Product
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.ui.cart.CartViewModel
import com.designbyark.layao.ui.home.HomeFragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProductDetailFragment : Fragment() {

    private var productId: String? = null
    private lateinit var navController: NavController

    private lateinit var mImage: ImageView
    private lateinit var mTitle: TextView
    private lateinit var mPrice: TextView
    private lateinit var mBrand: TextView
    private lateinit var mDiscount: TextView
    private lateinit var mOriginalPrice: TextView

    private lateinit var mAdd: ImageButton
    private lateinit var mSubtract: ImageButton
    private lateinit var mQuantity: TextView

    private lateinit var mAddToCart: Button

    private var quantity: Long = 1

    private var price: Double = 0.0
    private var discount: Double = 0.0
    private var stock: Double = 0.0

    private var brand: String = ""
    private var unit: String = ""
    private var image: String = ""
    private var mTag: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString(HomeFragment.PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection(PRODUCTS_COLLECTION)
        val document = productId?.let { collection.document(it) }

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        val root = inflater.inflate(R.layout.fragment_product_detail, container, false)

        findingViews(root)
        getData(document)

        mAdd.setOnClickListener {
            quantity += 1
            mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
        }

        mSubtract.setOnClickListener {
            quantity -= 1
            if (quantity <= 1) {
                quantity = 1
            }
            mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
        }

        mAddToCart.setOnClickListener {

            val cart = Cart()
            cart.brand = brand
            cart.discount = discount
            cart.image = image
            cart.price = price
            cart.tag = mTag
            cart.title = title
            cart.unit = unit
            cart.quantity = quantity
            if (discount > 0) {
                cart.total = setDiscountPrice(price, discount) * quantity
            } else {
                cart.total = price * quantity
            }
            cartViewModel.insert(cart)
            Toast.makeText(requireActivity(), "Added to cart!", Toast.LENGTH_LONG).show()

        }

        return root
    }


    private fun getData(document: DocumentReference?) {
        document?.get()?.addOnSuccessListener { documentSnapshot ->
            val model = documentSnapshot.toObject(Product::class.java)
            if (model != null) {

                brand = model.brand
                discount = model.discount.toDouble()
                image = model.image
                price = model.price.toDouble()
                mTag = model.tag
                title = model.title
                unit = model.unit
                stock = model.stock.toDouble()

                Glide.with(requireActivity()).load(model.image).into(mImage)
                mTitle.text = model.title
                (requireActivity() as AppCompatActivity).run {
                    supportActionBar?.setTitle(model.title)
                }
                if (discount > 0) {
                    mPrice.text = String.format(
                        Locale.getDefault(),
                        "Rs. %.0f/%s", setDiscountPrice(price, discount), unit
                    )
                    mOriginalPrice.text = String.format(
                        Locale.getDefault(),
                        "Rs. %.0f/%s", price, unit
                    )
                    mOriginalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    mPrice.text = String.format(
                        Locale.getDefault(),
                        "Rs. %.0f/%s", price, unit
                    )
                    mOriginalPrice.visibility = View.INVISIBLE
                    mDiscount.visibility = View.INVISIBLE
                }
                mBrand.text = model.brand
                if (discount > 0) {
                    mDiscount.text = String.format(
                        Locale.getDefault(),
                        "%.0f%% off", discount
                    )
                }
                mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
            }
        }
    }

    private fun findingViews(root: View) {
        mImage = root.findViewById(R.id.image)
        mTitle = root.findViewById(R.id.title)
        mPrice = root.findViewById(R.id.price)
        mBrand = root.findViewById(R.id.brand)
        mDiscount = root.findViewById(R.id.discount)
        mAdd = root.findViewById(R.id.add)
        mSubtract = root.findViewById(R.id.subtract)
        mQuantity = root.findViewById(R.id.quantity)
        mAddToCart = root.findViewById(R.id.add_to_cart)
        mOriginalPrice = root.findViewById(R.id.original_price)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
