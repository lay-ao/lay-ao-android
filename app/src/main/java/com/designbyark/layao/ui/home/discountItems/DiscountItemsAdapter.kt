package com.designbyark.layao.ui.home.discountItems

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.common.getSavingPrice
import com.designbyark.layao.common.setDiscountPrice
import com.designbyark.layao.data.Products
import com.designbyark.layao.ui.home.product.ProductViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    var context: Context,
    val itemClickListener: DiscountItemClickListener
) :
    FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_discount_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {
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
            val data = mutableMapOf<String, String>()
            data["id"] = snapshots.getSnapshot(holder.adapterPosition).id
            data["tag"] = model.tag
            itemClickListener.onDiscountItemClickListener(data)
        }
    }

    interface DiscountItemClickListener {
        fun onDiscountItemClickListener(productData: MutableMap<String, String>)
    }

}