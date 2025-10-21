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
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
}