package com.designbyark.layao

import android.util.Log
import com.designbyark.layao.util.LOG_TAG
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "User token: $token")
    }

}