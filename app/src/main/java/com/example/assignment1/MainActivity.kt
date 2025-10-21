package com.example.assignment1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.content.Intent
import com.example.assignment1.utils.FirebaseAuthManager

class MainActivity : AppCompatActivity() {
    private val authManager = FirebaseAuthManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Splash screen with 5-second delay as required
        Handler(Looper.getMainLooper()).postDelayed({
            if (!authManager.isUserLoggedIn()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@postDelayed
            }

            val current = authManager.getCurrentUser()
            if (current == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@postDelayed
            }

            // Check if first-time (no username set) and route to profile setup
            authManager.getUserData(current.userId) { user ->
                val nextIntent = if (user == null || user.username.isBlank()) {
                    Intent(this, signup::class.java)
                } else {
                    Intent(this, HomeScreen::class.java)
                }
                startActivity(nextIntent)
                finish()
            }
        }, 5000) // 5000 ms = 5 seconds
    }
}