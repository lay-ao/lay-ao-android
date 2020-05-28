package com.designbyark.layao.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.designbyark.layao.R
import com.designbyark.layao.data.ProductsData
import com.designbyark.layao.databinding.SearchListItemProductsBinding
import com.designbyark.layao.ui.home.HomeFragment

class ProductAdapter : PagedListAdapter<ProductsData, SearchProductViewHolder>(ProductAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchListItemProductsBinding.inflate(layoutInflater, parent, false)
        return SearchProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        val productsData = getItem(position)
        if (productsData != null) {
            holder.bind(productsData)
            holder.itemView.setOnClickListener {
                val args = Bundle()
                args.putString(HomeFragment.PRODUCT_ID, productsData.id)
                holder.itemView.findNavController()
                    .navigate(R.id.action_navigation_search_to_productDetailFragment, args)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<ProductsData>() {

        override fun areItemsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: ProductsData, newItem: ProductsData): Boolean {
            return oldItem.title == newItem.title
        }
    }

}