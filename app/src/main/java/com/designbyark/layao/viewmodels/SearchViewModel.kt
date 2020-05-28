package com.designbyark.layao.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.helper.android.filter.facet.FacetListAdapter
import com.algolia.instantsearch.helper.android.filter.state.connectPagedList
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.filter.facet.FacetListConnector
import com.algolia.instantsearch.helper.filter.facet.FacetListPresenterImpl
import com.algolia.instantsearch.helper.filter.facet.FacetSortCriterion
import com.algolia.instantsearch.helper.filter.facet.connectView
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.searcher.connectFilterState
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.designbyark.layao.adapters.ProductAdapter
import com.designbyark.layao.data.ProductsData
import com.designbyark.layao.viewholders.SearchFacetListViewHolder
import io.ktor.client.features.logging.LogLevel

const val APPLICATION_ID = "BQ4YKO5KVQ"
const val API_KEY = "3f30cea402713ca145a14a84ab5a148d"
const val INDEX_PRODUCTS = "Products"
// const val INDEX_BRANDS = "Products"
// const val INDEX_CATEGORIES = "Products"

class SearchViewModel : ViewModel() {

    private val client = ClientSearch(
        ApplicationID(APPLICATION_ID),
        APIKey(API_KEY),
        LogLevel.ALL
    )

    private val productsIndex = client.initIndex(IndexName(INDEX_PRODUCTS))
    private val searcher = SearcherSingleIndex(productsIndex)

    private val dataSourceEntry = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        ProductsData(
            hit.json.getPrimitive("objectID").content,
            hit.json.getPrimitive("title").content,
            hit.json.getPrimitive("brand").content,
            hit.json.getPrimitive("tag").content,
            hit.json.getPrimitive("image").content,
            hit.json.getObjectOrNull("_highlightResult")
        )
    }

    private val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()
    val products: LiveData<PagedList<ProductsData>> =
        LivePagedListBuilder(dataSourceEntry, pagedListConfig).build()
    val adapterProduct = ProductAdapter()

    private val filterState = FilterState()
    private val facetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("tag"),
        selectionMode =  SelectionMode.Multiple
    )

    private val facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending),
        limit = 100
    )

    val adapterFacet = FacetListAdapter(SearchFacetListViewHolder.Factory)

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))

    private val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += facetList
        connection += searcher.connectFilterState(filterState)
        connection += facetList.connectView(adapterFacet, facetPresenter)
        connection += filterState.connectPagedList(products)
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }

}