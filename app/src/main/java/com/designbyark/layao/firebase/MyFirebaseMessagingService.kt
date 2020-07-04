package com.designbyark.layao.firebase

import android.util.Log
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.util.sendTokenToFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "Passing token from onNewToken")
        Log.d(LOG_TAG, "User token: $token")
        sendTokenToFirestore(token)
    }

}