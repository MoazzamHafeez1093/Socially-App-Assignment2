package com.example.assignment1.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Manages FCM token registration and updates
 * Note: Token management moved to REST API - this is a stub for FCM token retrieval
 */
object FcmTokenManager {
    
    private const val TAG = "FcmTokenManager"
    
    /**
     * Request FCM token
     * Token is saved via REST API in SessionManager
     */
    fun registerFcmToken(onTokenReceived: (String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "FCM Token retrieved: $token")
                onTokenReceived(token)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get FCM token", e)
            }
    }
}
