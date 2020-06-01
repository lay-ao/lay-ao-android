package com.designbyark.layao.data

import android.os.Parcel
import android.os.Parcelable

data class Checkout(
    var cartTotal: Double,
    var totalItems: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(cartTotal)
        parcel.writeInt(totalItems)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Checkout> {
        override fun createFromParcel(parcel: Parcel): Checkout {
            return Checkout(parcel)
        }

        override fun newArray(size: Int): Array<Checkout?> {
            return arrayOfNulls(size)
        }
    }
}