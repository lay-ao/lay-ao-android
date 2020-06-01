package com.designbyark.layao.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
class Products() : Parcelable {

    @PropertyName("product_id")
    var productId: String = ""

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

    @PropertyName("stock")
    var stock: Long = 0

    @PropertyName("tag")
    var tag: String = ""

    @PropertyName("title")
    var title: String = ""

    @PropertyName("unit")
    var unit: String = ""

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString() ?: ""
        available = parcel.readByte() != 0.toByte()
        brand = parcel.readString() ?: ""
        discount = parcel.readDouble()
        image = parcel.readString() ?: ""
        price = parcel.readDouble()
        stock = parcel.readLong()
        tag = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
        unit = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeByte(if (available) 1 else 0)
        parcel.writeString(brand)
        parcel.writeDouble(discount)
        parcel.writeString(image)
        parcel.writeDouble(price)
        parcel.writeLong(stock)
        parcel.writeString(tag)
        parcel.writeString(title)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Products> {
        override fun createFromParcel(parcel: Parcel): Products {
            return Products(parcel)
        }

        override fun newArray(size: Int): Array<Products?> {
            return arrayOfNulls(size)
        }
    }

}