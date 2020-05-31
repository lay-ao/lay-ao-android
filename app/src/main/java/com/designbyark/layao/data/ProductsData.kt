package com.designbyark.layao.data

import com.algolia.instantsearch.core.highlighting.HighlightedString
import com.algolia.instantsearch.helper.highlighting.Highlightable
import com.algolia.search.model.Attribute
import kotlinx.serialization.json.JsonObject

data class ProductsData(
    val id: String,
    val title: String,
    val brand: String,
    val tag: String,
    val image: String,
    override val _highlightResult: JsonObject?
) : Highlightable {

    val highlightedTitle: HighlightedString?
        get() = getHighlight(Attribute("title"))

}