package com.designbyark.layao.ui.home.discountItems

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.common.getSavingPrice
import com.designbyark.layao.common.setDiscountPrice
import com.designbyark.layao.data.Product
import com.designbyark.layao.ui.home.product.ProductViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Product>,
    var context: Context,
    val itemClickListener: DiscountItemClickListener
) :
    FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_discount_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
        holder.run {
            setImage(model.image, context)
            setTitle(model.title)
            setDiscount(model.discount)
            setSavingPrice(
                getSavingPrice(
                    setDiscountPrice(model.price, model.discount),
                    model.price
                )
            )
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onDiscountItemClickListener(
                snapshots
                    .getSnapshot(holder.adapterPosition).id
            )
        }
    }

    interface DiscountItemClickListener {
        fun onDiscountItemClickListener(productId: String)
    }

}