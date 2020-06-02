package com.designbyark.layao.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName


/*
* isFavorite Properties
* -1 - Default
* 0 - False
* 1 - True
* */

@Entity(tableName = "favorites_table")
data class Favorites(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "productId")
    var productId: String = "",

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Int = -1,

    @ColumnInfo(name = "brand")
    var brand: String = "",

    @ColumnInfo(name = "discount")
    var discount: Double = 0.0,

    @ColumnInfo(name = "image")
    var image: String = "",

    @ColumnInfo(name = "price")
    var price: Double = 0.0,

    @ColumnInfo(name = "tag")
    var tag: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "unit")
    var unit: String = ""

)