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
        
        // Create a simple splash screen programmatically to avoid any layout issues
        createSimpleSplashScreen()

        // Splash screen with 5-second delay as required
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                // Always go to login to avoid Firebase initialization issues
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // If there's any error, try to go to login anyway
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e2: Exception) {
                    // If even that fails, just finish the activity
                    finish()
                }
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
