package com.designbyark.layao.data

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
class Category {

    @PropertyName("title")
    var title: String = ""

    @PropertyName("image")
    var image: String = ""
}