package com.designbyark.layao.ui.productdetail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.adapters.SimilarProductListAdapter
import com.designbyark.layao.data.Cart
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.FragmentProductDetailBinding
import com.designbyark.layao.util.*
import com.designbyark.layao.viewmodels.CartViewModel
import com.designbyark.layao.viewmodels.FavoritesViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductDetailFragment : Fragment(), SimilarProductListAdapter.ProductListItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private val args: ProductDetailFragmentArgs by navArgs()

    private lateinit var cartViewModel: CartViewModel
    private lateinit var favoriteViewModel: FavoritesViewModel
    private lateinit var binding: FragmentProductDetailBinding

    private var mAdapter: SimilarProductListAdapter? = null
    private var firebaseUser: FirebaseUser? = null
    private var isFavorite: Boolean = false

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
        favoriteViewModel = ViewModelProvider(requireActivity()).get(FavoritesViewModel::class.java)
        firestore = FirebaseFirestore.getInstance()
        val productCollection = firestore.collection(PRODUCTS_COLLECTION)

        val query = productCollection.whereEqualTo("tag", args.product.tag)
            .orderBy("title", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Products>()
            .setQuery(query, Products::class.java)
            .build()

        favoriteViewModel.getFavorite(args.product.productId).observe(requireActivity(), Observer {
            if (it == 0) {
                isFavorite = false
                binding.mFavorite.isSelected = false
            } else if (it == 1) {
                isFavorite = true
                binding.mFavorite.isSelected = true
            }
        })

        mAdapter = SimilarProductListAdapter(
            options,
            this,
            args.product.productId
        )
        setHorizontalListLayout(
            binding.mSimilarProductsRV,
            requireContext()
        )
        binding.mSimilarProductsRV.adapter = mAdapter

        val item = cartViewModel.getItem(args.product.productId)
        if (item != null) {
            binding.mAddToCart.text = "Already in cart"
            binding.mAddToCart.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#387BA2"))
            binding.mAddToCart.icon = view.context.getDrawable(R.drawable.ic_done)
            binding.mAddToCart.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        }
    }

    fun subtract() {
        quantity--
        if (quantity <= 1) {
            quantity = 1
        }
        binding.mQuantity.text =
            setQuantityPrice(
                args.product.price,
                quantity,
                args.product.discount,
                args.product.unit
            )
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
        cart.productId = args.product.productId
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

        val item = cartViewModel.getItem(args.product.productId)

        if (item != null) {
            cartViewModel.updateCart(cart)
            notifyUser("Cart Updated!", android.R.color.holo_blue_dark)
        } else {
            cartViewModel.insert(cart)
            notifyUser("Added to cart!", android.R.color.holo_green_dark)
        }
    }

    private fun notifyUser(message: String, @ColorRes color: Int) {
        binding.cartMessage.text = message
        binding.cartMessage.setTextColor(
            ContextCompat.getColor(binding.root.context, color)
        )
    }

    fun addToFav() {

        val favorite = Favorites()
        favorite.productId = args.product.productId
        favorite.brand = args.product.brand
        favorite.discount = args.product.discount
        favorite.image = args.product.image
        favorite.price = args.product.price
        favorite.tag = args.product.tag
        favorite.title = args.product.title
        favorite.unit = args.product.unit

        if (isFavorite) {
            isFavorite = false
            binding.mFavorite.isSelected = false
            favorite.isFavorite = 0
            favoriteViewModel.deleteAFavorite(favorite)
        } else {
            isFavorite = true
            binding.mFavorite.isSelected = true
            favorite.isFavorite = 1
            favoriteViewModel.insert(favorite)
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
        inflater.inflate(R.menu.product_detail_menu, menu)
    }

    override fun mProductListItemClickListener(product: Products) {
        val action = ProductDetailFragmentDirections.actionProductDetailFragmentSelf(product)
        findNavController().navigate(action)
    }


}
