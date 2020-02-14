package com.designbyark.layao.data

import com.designbyark.layao.data.favorite.Favorite
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

@IgnoreExtraProperties
class User : Serializable {

    @PropertyName("userId")
    var userId: String = ""

    @PropertyName("fullName")
    var fullName: String = ""

    @PropertyName("email")
    var email: String = ""

    @PropertyName("password")
    var password: String = ""

    @PropertyName("wallet")
    var wallet: Double = 0.0

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

}