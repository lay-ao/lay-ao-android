package com.designbyark.layao.data

import com.designbyark.layao.data.cart.Cart
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class Order {

    @PropertyName("orderId")
    var orderId: String = ""

    @PropertyName("name")
    var fullName: String = ""

    @PropertyName("contactNumber")
    var contactNumber: String = ""

    @PropertyName("houseNumber")
    var houseNumber: String = ""

    @PropertyName("block")
    var block: String = ""

    @PropertyName("address")
    var completeAddress: String = ""

    @PropertyName("comment")
    var comment: String = ""

    @PropertyName("items")
    var items: List<Cart> = emptyList()

    @PropertyName("orderTime")
    @ServerTimestamp
    var orderTime: Timestamp = Timestamp.now()

    @PropertyName("orderStatus")
    var orderStatus: Int = 0

    @PropertyName("total_items")
    var totalItems: Int = 0

    @PropertyName("grand_total")
    var grandTotal: Double = 0.0

}