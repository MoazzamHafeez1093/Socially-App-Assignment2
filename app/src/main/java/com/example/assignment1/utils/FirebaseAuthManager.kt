package com.example.assignment1.utils

import android.content.Context
import android.content.Intent
import com.example.assignment1.HomeScreen
import com.example.assignment1.LoginActivity
import com.example.assignment1.models.User

class FirebaseAuthManager {
    // Temporary implementation - will be replaced with real Firebase
    private var currentUser: User? = null

    fun signUp(email: String, password: String, username: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        // Simulate Firebase signup
        if (email.isNotEmpty() && password.length >= 6) {
            currentUser = User(
                uid = "temp_${System.currentTimeMillis()}",
                username = username,
                email = email
            )
            onComplete(true, null)
        } else {
            onComplete(false, "Invalid credentials")
        }
    }

    fun signIn(email: String, password: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        // Simulate Firebase signin
        if (email.isNotEmpty() && password.isNotEmpty()) {
            currentUser = User(
                uid = "temp_${System.currentTimeMillis()}",
                email = email
            )
            onComplete(true, null)
        } else {
            onComplete(false, "Invalid credentials")
        }
    }

    fun signOut(context: Context) {
        currentUser = null
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    fun getUserData(uid: String, onComplete: (User?) -> Unit) {
        onComplete(currentUser)
    }

    fun updateUserProfile(user: User, onComplete: (Boolean) -> Unit) {
        currentUser = user
        onComplete(true)
    }
}
