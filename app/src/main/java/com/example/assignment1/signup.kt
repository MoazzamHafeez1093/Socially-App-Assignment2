package com.example.assignment1

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.appbar.MaterialToolbar
import android.widget.EditText
import com.example.assignment1.utils.FirebaseAuthManager
import java.util.*


// Signup screen class where user can enter details and create an account
class signup : AppCompatActivity() {

    // Image view to display profile picture chosen by user
    private lateinit var profileImageView: ImageView
    // Camera button to open gallery for picking an image
    private lateinit var cameraButton: ImageButton

    // Date of birth input field
    private lateinit var dobEditText: TextInputEditText

    // Keep track of selected image Uri (so we can send it to next activity)
    private var selectedImageUri: Uri? = null

    private lateinit var authManager: FirebaseAuthManager

    // Launcher for image picker (opens gallery and sets selected image to profileImageView)
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it   // save Uri for later use
            profileImageView.setImageURI(it)  // show the chosen image inside ImageView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a simple signup screen programmatically to avoid any layout issues
        createSimpleSignupScreen()

        try {
            authManager = FirebaseAuthManager()
        } catch (e: Exception) {
            // If Firebase fails to initialize, continue without it
        }

        // Simple signup screen - no complex findViewById calls needed

        // Simple signup screen - all functionality handled in createSimpleSignupScreen()
    }
    
    private fun createSimpleSignupScreen() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setBackgroundColor(android.graphics.Color.parseColor("#784A34"))
            setPadding(50, 50, 50, 50)
        }
        
        val logo = android.widget.TextView(this).apply {
            text = "Create Account"
            textSize = 32f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 50
            }
        }
        
        val usernameInput = android.widget.EditText(this).apply {
            hint = "Username"
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
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
        
        val createButton = android.widget.Button(this).apply {
            text = "Create Account"
            setBackgroundColor(android.graphics.Color.WHITE)
            setTextColor(android.graphics.Color.parseColor("#784A34"))
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20
            }
            setOnClickListener {
                // Simple signup - just go to home screen
                val intent = Intent(this@signup, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        layout.addView(logo)
        layout.addView(usernameInput)
        layout.addView(emailInput)
        layout.addView(passwordInput)
        layout.addView(createButton)
        setContentView(layout)
    }
}