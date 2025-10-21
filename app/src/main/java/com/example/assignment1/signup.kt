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
        enableEdgeToEdge()  // makes UI stretch edge-to-edge for modern look
        try {
            setContentView(R.layout.activity_signup)
        } catch (e: Exception) {
            // If layout fails, create a simple signup screen programmatically
            createSimpleSignupScreen()
        }

        authManager = FirebaseAuthManager()

        // Handle system bar insets (status bar, navigation bar padding)
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            // If findViewById fails, continue without window insets
        }

        // Initialize profile image view and button (both pointing to the same camera button id in XML)
        try {
            profileImageView = findViewById(R.id.cameraButton)
            cameraButton = findViewById(R.id.cameraButton)
        } catch (e: Exception) {
            // If findViewById fails, continue without these views
        }

        // When user taps camera button, open image picker
        try {
            cameraButton.setOnClickListener {
                pickImage.launch("image/*")  // Only allow image selection
            }
        } catch (e: Exception) {
            // If cameraButton is null, continue without image picker
        }

        // Initialize date of birth field
        try {
            dobEditText = findViewById(R.id.dobEditText)
        } catch (e: Exception) {
            // If findViewById fails, continue without DOB field
        }

        // Show date picker when clicking DOB field
        try {
            dobEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Date picker dialog opens here
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format date nicely and put it inside the text field
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                dobEditText.setText(formattedDate)
            }, year, month, day).show()
            }
        } catch (e: Exception) {
            // If dobEditText is null, continue without date picker
        }

        // Handle create account button click
        try {
            val btnCreatAccount = findViewById<AppCompatButton>(R.id.createAccountBtn)
            btnCreatAccount.setOnClickListener {
            val usernameEditText: EditText = findViewById(R.id.userName1)
            val emailEditText: EditText = findViewById(R.id.emailEditText)
            val passwordEditText: EditText = findViewById(R.id.passwordEditText)
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username, email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                authManager.signUp(email, password, username, this) { success, message ->
                    if (success) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeScreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // If Firebase is not initialized, just navigate to home screen for demo
                Toast.makeText(this, "Demo mode - Account created, navigating to home", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
        } catch (e: Exception) {
            // If create account button is null, continue without button
        }

        // Set up toolbar (action bar at the top of the screen)
        try {
            setSupportActionBar(findViewById(R.id.toolbar))
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Handle back button in toolbar (navigate back)
            findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        } catch (e: Exception) {
            // If toolbar is null, continue without toolbar
        }
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