package com.designbyark.layao.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class Banner {

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

}