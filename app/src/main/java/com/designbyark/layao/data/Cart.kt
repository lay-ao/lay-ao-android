package com.designbyark.layao.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
class Cart() {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "productId")
    var productId: String = ""

    @ColumnInfo(name = "brand")
    var brand: String = ""

    @ColumnInfo(name = "discount")
    var discount: Double = 0.0

    @ColumnInfo(name = "image")
    var image: String = ""

    @ColumnInfo(name = "price")
    var price: Double = 0.0

    @ColumnInfo(name = "tag")
    var tag: String = ""

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "unit")
    var unit: String = ""

    @ColumnInfo(name = "quantity")
    var quantity: Long = 0

    @ColumnInfo(name = "total")
    var total: Double = 0.0

    @ColumnInfo(name = "stock")
    var stock: Long = 0


}