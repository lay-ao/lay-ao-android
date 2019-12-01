package com.designbyark.layao.data.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
class Favorite {

    @PrimaryKey
    @ColumnInfo(name = "db_id")
    var dbId: String = ""

    @ColumnInfo(name = "image")
    var image: String = ""

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "is_favorite")
    var favorite: Int = 0

}