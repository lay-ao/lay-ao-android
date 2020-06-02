package com.designbyark.layao.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
class Order() : Parcelable {

    @PropertyName("orderId")
    var orderId: String = ""

    @PropertyName("name")
    var fullName: String = ""

    @PropertyName("contactNumber")
    var contactNumber: String = ""

    @PropertyName("houseNumber")
    var houseNumber: String = ""

    @PropertyName("block")
    var block: Int = 0

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
    var orderStatus: Long = 0

    @PropertyName("total_items")
    var totalItems: Int = 0

    @PropertyName("grand_total")
    var grandTotal: Double = 0.0

    @PropertyName("userId")
    var userId: String = ""

    @PropertyName("cancelled")
    var cancelled: Boolean = false

    constructor(parcel: Parcel) : this() {
        orderId = parcel.readString() ?: ""
        fullName = parcel.readString() ?: ""
        contactNumber = parcel.readString() ?: ""
        houseNumber = parcel.readString() ?: ""
        block = parcel.readInt()
        completeAddress = parcel.readString() ?: ""
        comment = parcel.readString() ?: ""
        orderTime = parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp.now()
        orderStatus = parcel.readLong()
        totalItems = parcel.readInt()
        grandTotal = parcel.readDouble()
        userId = parcel.readString() ?: ""
        cancelled = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(fullName)
        parcel.writeString(contactNumber)
        parcel.writeString(houseNumber)
        parcel.writeInt(block)
        parcel.writeString(completeAddress)
        parcel.writeString(comment)
        parcel.writeParcelable(orderTime, flags)
        parcel.writeLong(orderStatus)
        parcel.writeInt(totalItems)
        parcel.writeDouble(grandTotal)
        parcel.writeString(userId)
        parcel.writeByte(if (cancelled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }

}