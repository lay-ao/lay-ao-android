package com.designbyark.layao.helper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.data.cart.CartDao

@Database(entities = arrayOf(Cart::class), version = 1, exportSchema = false)
abstract class LayAoRoomDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao

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
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}