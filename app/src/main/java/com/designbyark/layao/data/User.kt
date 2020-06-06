package com.designbyark.layao.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

@IgnoreExtraProperties
class User() : Parcelable {

    @PropertyName("userId")
    var userId: String = ""

    @PropertyName("fullName")
    var fullName: String = ""

    @PropertyName("email")
    var email: String = ""

    @PropertyName("password")
    var password: String = ""

    @PropertyName("completeAddress")
    var completeAddress: String = ""

    @PropertyName("houseNumber")
    var houseNumber: String = ""

    @PropertyName("blockNumber")
    var blockNumber: Int = 0

    @PropertyName("contact")
    var contact: String = ""

    @PropertyName("gender")
    var gender: Int = 0

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString() ?: ""
        fullName = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
        password = parcel.readString() ?: ""
        completeAddress = parcel.readString() ?: ""
        houseNumber = parcel.readString() ?: ""
        blockNumber = parcel.readInt()
        contact = parcel.readString() ?: ""
        gender = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(completeAddress)
        parcel.writeString(houseNumber)
        parcel.writeInt(blockNumber)
        parcel.writeString(contact)
        parcel.writeInt(gender)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}