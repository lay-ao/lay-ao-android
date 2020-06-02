package com.designbyark.layao.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class Banner() : Parcelable {

    @PropertyName("active")
    var active: Boolean = false

    @PropertyName("code")
    var code: String = ""

    @PropertyName("codeState")
    var codeState: Boolean = false

    @PropertyName("description")
    var description: String = ""

    @PropertyName("id")
    var id: String = ""

    @PropertyName("image")
    var image: String = ""

    @PropertyName("title")
    var title: String = ""

    @ServerTimestamp
    @PropertyName("validity")
    var validity: Date? = null

    constructor(parcel: Parcel) : this() {
        active = parcel.readByte() != 0.toByte()
        code = parcel.readString() ?: ""
        codeState = parcel.readByte() != 0.toByte()
        description = parcel.readString() ?: ""
        id = parcel.readString() ?: ""
        image = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(code)
        parcel.writeByte(if (codeState) 1 else 0)
        parcel.writeString(description)
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Banner> {
        override fun createFromParcel(parcel: Parcel): Banner {
            return Banner(parcel)
        }

        override fun newArray(size: Int): Array<Banner?> {
            return arrayOfNulls(size)
        }
    }

}