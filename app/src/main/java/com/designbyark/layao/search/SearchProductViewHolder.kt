package com.designbyark.layao.search

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helper.android.highlighting.toSpannedString
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.search_list_item_products.view.*

class SearchProductViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(productsData: ProductsData, context: Context) {
        view.title.text = productsData.highlightedTitle?.toSpannedString() ?: productsData.title
        view.brand.text = productsData.brand
        view.product_tag.text = productsData.tag
        Glide.with(context).load(productsData.image).into(view.image)
    }
}