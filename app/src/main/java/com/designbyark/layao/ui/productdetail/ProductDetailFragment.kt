package com.designbyark.layao.ui.productdetail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

class ProductDetailFragment : Fragment(), SimilarProductListAdapter.ProductListItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private val args: ProductDetailFragmentArgs by navArgs()

    private lateinit var cartViewModel: CartViewModel
    private lateinit var binding: FragmentProductDetailBinding

    private var mAdapter: SimilarProductListAdapter? = null
    private var firebaseUser: FirebaseUser? = null
    private var documentExists: Boolean = false

    private var discount: Double = 0.0

    private var quantity: Long = 1
    private var stock: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        binding.detail = this
        binding.product = args.product
        return binding.root
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            supportActionBar?.setTitle(args.product.title)
        }
        stock = args.product.stock
        binding.mQuantity.text = setQuantityPrice(
            args.product.price,
            quantity,
            args.product.discount,
            args.product.unit
        )

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)
        firestore = FirebaseFirestore.getInstance()
        val productCollection = firestore.collection(PRODUCTS_COLLECTION)
        if (firebaseUser != null) {
            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                .collection("Favorites").document(args.product.productId)
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentExists = true
                        binding.mFavorite.isSelected = true
                    }
                }
        }


        val query = productCollection.whereEqualTo("tag", args.product.tag)
            .orderBy("title", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        mAdapter = SimilarProductListAdapter(
            options,
            this,
            args.product.productId
        )
        setHorizontalListLayout(binding.mSimilarProductsRV, requireContext())
        binding.mSimilarProductsRV.adapter = mAdapter
    }

    fun subtract() {
        quantity--
        if (quantity <= 1) {
            quantity = 1
        }
        binding.mQuantity.text =
            setQuantityPrice(args.product.price, quantity, args.product.discount, args.product.unit)
    }

    fun add() {
        if (quantity != stock) {
            quantity++
            binding.mQuantity.text = setQuantityPrice(
                args.product.price,
                quantity,
                args.product.discount,
                args.product.unit
            )
        } else {
            Toast.makeText(requireContext(), "Max Stock Reached!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToCart() {
        val cart = Cart()
        cart.brand = args.product.brand
        cart.discount = args.product.discount
        cart.image = args.product.image
        cart.price = args.product.price
        cart.tag = args.product.tag
        cart.title = args.product.title
        cart.unit = args.product.unit
        cart.quantity = quantity
        cart.stock = args.product.stock
        if (discount > 0) {
            cart.total = findDiscountPrice(args.product.price, args.product.discount) * quantity
        } else {
            cart.total = args.product.price * quantity
        }
        cartViewModel.insert(cart)
        Toast.makeText(requireActivity(), "Added to cart!", Toast.LENGTH_LONG).show()
    }

    fun addToFav() {
        if (firebaseUser == null) {
            Log.d(LOG_TAG, "Adding to Favorite: firebaseUser == null.")
            return
        }

        if (documentExists) {
            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                .collection("Favorites").document(args.product.productId)
                .delete().addOnCompleteListener { task ->
                    if (task.isSuccessful && task.isComplete) {
                        documentExists = false
                        binding.mFavorite.isSelected = false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(LOG_TAG, exception.localizedMessage, exception)
                }
        }

        if (!documentExists) {
            val favorites = Favorites()
            favorites.productId = args.product.productId
            favorites.brand = args.product.brand
            favorites.discount = args.product.discount
            favorites.image = args.product.image
            favorites.price = args.product.price
            favorites.tag = args.product.tag
            favorites.title = args.product.title
            favorites.unit = args.product.unit

            firestore.collection(USERS_COLLECTION).document(firebaseUser!!.uid)
                .collection("Favorites").document(args.product.productId)
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
