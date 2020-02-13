package com.designbyark.layao.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.data.cart.CartDao
import com.designbyark.layao.data.favorite.Favorite
import com.designbyark.layao.data.favorite.FavoriteDao


@Database(entities = arrayOf(Cart::class, Favorite::class), version = 9, exportSchema = false)
abstract class LayAoRoomDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        @Volatile
        private var INSTANCE: LayAoRoomDatabase? = null

        fun getDatabase(context: Context): LayAoRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LayAoRoomDatabase::class.java,
                    "cart_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}