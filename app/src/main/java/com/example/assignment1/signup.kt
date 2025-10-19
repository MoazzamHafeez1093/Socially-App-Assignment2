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
        setContentView(R.layout.activity_signup)

        authManager = FirebaseAuthManager()

        // Handle system bar insets (status bar, navigation bar padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize profile image view and button (both pointing to the same camera button id in XML)
        profileImageView = findViewById(R.id.cameraButton)
        cameraButton = findViewById(R.id.cameraButton)

        // When user taps camera button, open image picker
        cameraButton.setOnClickListener {
            pickImage.launch("image/*")  // Only allow image selection
        }

        // Initialize date of birth field
        dobEditText = findViewById(R.id.dobEditText)

        // Show date picker when clicking DOB field
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

        // Handle create account button click
        val btnCreatAccount = findViewById<AppCompatButton>(R.id.createAccountBtn)
        btnCreatAccount.setOnClickListener {
            // Get username field
            val usernameEditText: EditText = findViewById(R.id.userName1)
            val username = usernameEditText.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For now, just navigate to home screen
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }

        // Set up toolbar (action bar at the top of the screen)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle back button in toolbar (navigate back)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}