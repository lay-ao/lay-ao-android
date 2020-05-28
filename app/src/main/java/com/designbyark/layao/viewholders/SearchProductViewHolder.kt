package com.designbyark.layao.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.ProductsData
import com.designbyark.layao.databinding.SearchListItemProductsBinding

class SearchProductViewHolder(private val binding: SearchListItemProductsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(productsData: ProductsData) {
        binding.data = productsData
        binding.executePendingBindings()
    }
}