package com.designbyark.layao.data

import com.google.firebase.firestore.PropertyName

class Product {

    @PropertyName("available")
    var available: Boolean = false

    @PropertyName("brand")
    var brand: String = ""

    @PropertyName("discount")
    var discount: Long = 0

    @PropertyName("image")
    var image: String = ""

    @PropertyName("newArrival")
    var newArrival: Boolean = false

    @PropertyName("price")
    var price: Long = 0

    @PropertyName("stock")
    var stock: Long = 0

    @PropertyName("tag")
    var tag: String = ""

    @PropertyName("title")
    var title: String = ""

    @PropertyName("unit")
    var unit: String = ""

}