package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No need to set content view for a splash screen that just redirects

        auth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, go straight to HomeScreen
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        } else {
            // No user is signed in, go to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // Finish this activity so the user can't navigate back to it
        finish()
    }
}
