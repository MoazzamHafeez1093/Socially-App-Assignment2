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
        try {
            setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            // If layout fails, create a simple splash screen programmatically
            createSimpleSplashScreen()
        }
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            // If findViewById fails, continue without window insets
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
    
    private fun createSimpleSplashScreen() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(android.graphics.Color.WHITE)
        }
        
        val logo = android.widget.TextView(this).apply {
            text = "Socially"
            textSize = 48f
            setTextColor(android.graphics.Color.parseColor("#784A34"))
            gravity = android.view.Gravity.CENTER
        }
        
        val subtitle = android.widget.TextView(this).apply {
            text = "from SMD"
            textSize = 20f
            setTextColor(android.graphics.Color.GRAY)
            gravity = android.view.Gravity.CENTER
        }
        
        layout.addView(logo)
        layout.addView(subtitle)
        setContentView(layout)
    }
}