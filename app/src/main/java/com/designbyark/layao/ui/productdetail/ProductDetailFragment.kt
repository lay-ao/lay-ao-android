package com.designbyark.layao.ui.productdetail

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.adapters.SimilarProductListAdapter
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.data.Products
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.databinding.FragmentProductDetailBinding
import com.designbyark.layao.ui.home.HomeFragment
import com.designbyark.layao.viewmodels.CartViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class ProductDetailFragment : Fragment(), SimilarProductListAdapter.ProductListItemClickListener {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var binding: FragmentProductDetailBinding

    private var mAdapter: SimilarProductListAdapter? = null
    private var firebaseUser: FirebaseUser? = null

    private var productId: String? = null
    private var productTag: String? = null

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
            productId = it.getString(HomeFragment.PRODUCT_ID) ?: ""
            productTag = it.getString(HomeFragment.PRODUCT_TAG) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        binding.product = this
        return binding.root
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        val firestore = FirebaseFirestore.getInstance()
        val productCollection = firestore.collection(PRODUCTS_COLLECTION)
        if (firebaseUser != null) {
            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                .collection("Favorites").document(productId!!)
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentExists = true
                        binding.mFavorite.isSelected = true
                    }
                }
        }

        Log.d(LOG_TAG, "Value ${productTag!!}")
        getData(view, firestore)


        val query = productCollection.whereEqualTo("tag", productTag!!)
            .orderBy("title", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = SimilarProductListAdapter(
            options,
            this,
            productId!!
        )
        setHorizontalListLayout(binding.mSimilarProductsRV, requireContext())
        binding.mSimilarProductsRV.adapter = mAdapter
    }

    fun subtract() {
        quantity--
        if (quantity <= 1) {
            quantity = 1
        }
        binding.mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
    }

    fun add() {
        if (quantity != stock) {
            quantity++
            binding.mQuantity.text = setQuantityPrice(price, quantity, discount, unit)
        } else {
            Toast.makeText(requireContext(), "Max Stock Reached!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToCart() {
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
            cart.total = findDiscountPrice(price, discount) * quantity
        } else {
            cart.total = price * quantity
        }
        cartViewModel.insert(cart)
        Toast.makeText(requireActivity(), "Added to cart!", Toast.LENGTH_LONG).show()
    }


    override fun onStart() {
        super.onStart()
        if (mAdapter != null) {
            mAdapter?.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter?.stopListening()
        }
    }

    @ExperimentalStdlibApi
    private fun getData(view: View, firestore: FirebaseFirestore) {
        firestore.collection(PRODUCTS_COLLECTION).document(productId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val model = documentSnapshot.toObject(Products::class.java)
                if (model != null) {

                    brand = model.brand.capitalize(Locale.ENGLISH)
                    discount = model.discount
                    image = model.image
                    price = model.price
                    mTag = model.tag
                    title = model.title
                    unit = model.unit
                    stock = model.stock

                    Glide.with(view.context).load(image)
                        .placeholder(circularProgressBar(requireContext())).into(binding.mImage)
                    binding.mTitle.text = title
                    (requireActivity() as AppCompatActivity).run {
                        supportActionBar?.setTitle(model.title)
                    }
                    if (discount > 0) {
                        binding.mPrice.text = String.format(
                            Locale.getDefault(),
                            "Rs. %.0f/%s", findDiscountPrice(price, discount), unit
                        )
                        binding.mOriginalPrice.text = String.format(
                            Locale.getDefault(),
                            "Rs. %.0f/%s", price, unit
                        )
                        binding.mOriginalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        binding.mPrice.text = String.format(
                            Locale.getDefault(),
                            "Rs. %.0f/%s", price, unit
                        )
                        binding.mPrice.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorTextPrimary
                            )
                        )
                        binding.mOriginalPrice.visibility = View.GONE
                        binding.mDiscount.visibility = View.GONE
                    }
                    binding.mBrand.text = brand
                    if (discount > 0) {
                        binding.mDiscount.text = String.format(
                            Locale.getDefault(),
                            "%.0f%% off ", discount
                        )
                    }
                    binding.mQuantity.text = setQuantityPrice(price, quantity, discount, unit)

                    binding.mFavorite.setOnClickListener {

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
                                        binding.mFavorite.isSelected = false
                                        return@addOnCompleteListener
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(LOG_TAG, exception.localizedMessage, exception)
                                }
                        } else {
                            val favorites =
                                Favorites()
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
                                        binding.mFavorite.isSelected = true
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(LOG_TAG, exception.localizedMessage, exception)
                                }
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun mProductListItemClickListener(productData: MutableMap<String, String>) {
        val args = Bundle()
        args.putString(HomeFragment.PRODUCT_ID, productData["id"])
        args.putString(HomeFragment.PRODUCT_TAG, productData["tag"])
        findNavController().navigate(R.id.action_productDetailFragment_self, args)
    }


}
