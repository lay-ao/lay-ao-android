package com.designbyark.layao.data.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
class Cart() {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "brand")
    var brand: String = ""

    @ColumnInfo(name = "discount")
    var discount: Long = 0

    @ColumnInfo(name = "image")
    var image: String = ""

    @ColumnInfo(name = "price")
    var price: Long = 0

    @ColumnInfo(name = "tag")
    var tag: String = ""

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "unit")
    var unit: String = ""

    @ColumnInfo(name = "quantity")
    var quantity: Long = 0

}