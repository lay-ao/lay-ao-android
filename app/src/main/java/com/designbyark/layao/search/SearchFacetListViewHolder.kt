package com.designbyark.layao.search

import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helper.android.filter.facet.FacetListViewHolder
import com.algolia.instantsearch.helper.android.inflate
import com.algolia.search.model.search.Facet
import com.designbyark.layao.R
import kotlinx.android.synthetic.main.facet_item.view.*
import java.util.*

class SearchFacetListViewHolder(view: View) : FacetListViewHolder(view) {

    @ExperimentalStdlibApi
    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
        view.setOnClickListener(onClickListener)
        view.facetCount.text = facet.count.toString()
        view.facetCount.visibility = View.VISIBLE
        view.icon.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        view.facetName.text = facet.value.capitalize(Locale.ENGLISH)
    }

    object Factory : FacetListViewHolder.Factory {

        override fun createViewHolder(parent: ViewGroup): FacetListViewHolder {
            return SearchFacetListViewHolder(parent.inflate(R.layout.facet_item))
        }

    }

}