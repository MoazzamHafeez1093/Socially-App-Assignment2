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
            try {
                // For now, always go to login to avoid Firebase initialization issues
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                // If there's any error, go to login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 5000) // 5000 ms = 5 seconds
    }
}