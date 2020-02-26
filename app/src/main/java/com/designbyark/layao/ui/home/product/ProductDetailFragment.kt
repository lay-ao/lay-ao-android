package com.designbyark.layao.ui.home.product


import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Products
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.ui.cart.CartViewModel
import com.designbyark.layao.ui.favorites.Favorites
import com.designbyark.layao.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_facet.*
import java.util.*

class ProductDetailFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
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
    private lateinit var mFavButton: ImageButton
    private lateinit var mQuantity: TextView

    private lateinit var mAddToCart: Button

    private var documentExists: Boolean = false

    private var price: Double = 0.0
    private var discount: Double = 0.0

    private var quantity: Long = 1
    private var stock: Long = 0

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
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

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

        if (firebaseUser != null) {
            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                .collection("Favorites").document(productId!!)
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentExists = true
                        mFavButton.isSelected = true
                    }
                }
        }

        getData(firestore)

        mAdd.setOnClickListener {
            if (quantity != stock) {
                quantity++
                mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
            } else {
                Toast.makeText(requireContext(), "Max Stock Reached!", Toast.LENGTH_SHORT).show()
            }
        }

        mSubtract.setOnClickListener {
            quantity--
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
            cart.stock = stock
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


    private fun getData(firestore: FirebaseFirestore) {
        firestore.collection(PRODUCTS_COLLECTION).document(productId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val model = documentSnapshot.toObject(Products::class.java)
                if (model != null) {

                    brand = model.brand
                    discount = model.discount
                    image = model.image
                    price = model.price
                    mTag = model.tag
                    title = model.title
                    unit = model.unit
                    stock = model.stock

                    Glide.with(requireContext()).load(model.image)
                        .placeholder(circularProgressBar(requireContext())).into(mImage)
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

                    mFavButton.setOnClickListener {


                        if (firebaseUser == null) {
                            Log.d(LOG_TAG, "Adding to Favorite: firebaseUser == null.")
                            return@setOnClickListener
                        }

                        if (documentExists) {
                            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                                .collection("Favorites").document(productId!!)
                                .delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful && task.isComplete) {
                                        documentExists = false
                                        mFavButton.isSelected = false
                                        return@addOnCompleteListener
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(LOG_TAG, exception.localizedMessage, exception)
                                }
                        }

                        val favorites = Favorites()
                        favorites.available = model.available
                        favorites.brand = model.brand
                        favorites.discount = model.discount
                        favorites.image = model.image
                        favorites.price = model.price
                        favorites.tag = model.tag
                        favorites.title = model.title
                        favorites.unit = model.unit

                        firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                            .collection("Favorites").document(productId!!)
                            .set(favorites).addOnCompleteListener { task ->
                                if (task.isSuccessful && task.isComplete) {
                                    mFavButton.isSelected = true
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(LOG_TAG, exception.localizedMessage, exception)
                            }
                    }
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
        mFavButton = root.findViewById(R.id.fav_button)
    }

}
