package com.example.assignment1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import com.example.assignment1.utils.FirebaseAuthManager

class LoginActivity : AppCompatActivity() {
    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContentView(R.layout.activity_login)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            // If layout fails, create a simple login screen programmatically
            createSimpleLoginScreen()
        }

        authManager = FirebaseAuthManager()

        // Check if user is already logged in
        try {
            if (authManager.isUserLoggedIn()) {
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
                return
            }
        } catch (e: Exception) {
            // If Firebase is not initialized, continue to login
        }

        val btnSignUp = findViewById<Button>(R.id.signupBtn)
        btnSignUp.setOnClickListener {
            val intentSignup = Intent(this, signup::class.java)
            startActivity(intentSignup)
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin2)
        btnLogin.setOnClickListener {
            val emailEditText: EditText = findViewById(R.id.emailTextBox)
            val passwordEditText: EditText = findViewById(R.id.passwordTextBox)
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                authManager.signIn(email, password, this) { success, error ->
                    if (success) {
                        val intent = Intent(this, HomeScreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, error ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // If Firebase is not initialized, just navigate to home screen for demo
                Toast.makeText(this, "Demo mode - navigating to home", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun createSimpleLoginScreen() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(android.graphics.Color.WHITE)
            setPadding(50, 50, 50, 50)
        }
        
        val logo = android.widget.TextView(this).apply {
            text = "Socially"
            textSize = 48f
            setTextColor(android.graphics.Color.parseColor("#784A34"))
            gravity = android.view.Gravity.CENTER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 50
            }
        }
        
        val emailInput = android.widget.EditText(this).apply {
            hint = "Email"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
        }
        
        val passwordInput = android.widget.EditText(this).apply {
            hint = "Password"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
        }
        
        val loginButton = android.widget.Button(this).apply {
            text = "Login"
            setBackgroundColor(android.graphics.Color.parseColor("#784A34"))
            setTextColor(android.graphics.Color.WHITE)
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
            setOnClickListener {
                // Simple login - just go to home screen
                val intent = Intent(this@LoginActivity, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        val signupButton = android.widget.Button(this).apply {
            text = "Sign Up"
            setTextColor(android.graphics.Color.parseColor("#784A34"))
            background = null
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                val intent = Intent(this@LoginActivity, signup::class.java)
                startActivity(intent)
            }
        }
        
        layout.addView(logo)
        layout.addView(emailInput)
        layout.addView(passwordInput)
        layout.addView(loginButton)
        layout.addView(signupButton)
        setContentView(layout)
    }
}