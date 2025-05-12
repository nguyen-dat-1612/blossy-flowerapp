package com.blossy.flowerstore.utils

import com.google.firebase.messaging.FirebaseMessaging

class FcmManager(private val firebaseMessaging: FirebaseMessaging) {

    fun enableFCM(onSuccess: (String) -> Unit, onError: (Exception?) -> Unit) {
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result)
            } else {
                onError(task.exception)
            }
        }
    }

    fun disableFCM(onError: (Exception?) -> Unit) {
        firebaseMessaging.deleteToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError(task.exception)
            }
        }
    }
}