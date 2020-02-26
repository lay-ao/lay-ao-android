package com.designbyark.layao.ui.favorites

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
class Favorites {

    @PropertyName("available")
    var available: Boolean = false

    @PropertyName("brand")
    var brand: String = ""

    @PropertyName("discount")
    var discount: Double = 0.0

    @PropertyName("image")
    var image: String = ""

    @PropertyName("price")
    var price: Double = 0.0

    @PropertyName("tag")
    var tag: String = ""

    @PropertyName("title")
    var title: String = ""

    @PropertyName("unit")
    var unit: String = ""

}