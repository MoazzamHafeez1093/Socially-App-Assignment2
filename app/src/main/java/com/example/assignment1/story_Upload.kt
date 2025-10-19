package com.example.assignment1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.assignment1.utils.Base64Image
import com.example.assignment1.utils.FirebaseAuthManager
import com.google.firebase.database.FirebaseDatabase

class story_Upload : AppCompatActivity() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val authManager = FirebaseAuthManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_view_own)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the profile image URI from the intent
        val imageUriString = intent.getStringExtra("PROFILE_IMAGE_URI")
        val profileImageView = findViewById<ImageView>(R.id.profile_image)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            profileImageView.setImageURI(imageUri)

            // Upload as Base64 to Realtime DB as a story expiring in 24h
            val user = authManager.getCurrentUser()
            if (user != null) {
                val base64 = Base64Image.uriToBase64(this, imageUri, 70)
                if (base64 != null) {
                    val storyId = database.reference.child("stories").push().key
                    if (storyId != null) {
                        // Get user data to include username
                        authManager.getUserData(user.uid) { userData ->
                            val username = userData?.username ?: "User"
                            val storyData = mapOf(
                                "storyId" to storyId,
                                "userId" to user.uid,
                                "username" to username,
                                "imageBase64" to base64,
                                "timestamp" to System.currentTimeMillis(),
                                "expiresAt" to (System.currentTimeMillis() + 24L * 60 * 60 * 1000)
                            )
                            database.reference.child("stories").child(storyId).setValue(storyData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Story uploaded successfully! Expires in 24 hours", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to upload story", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
        val intentClose = findViewById<ConstraintLayout>(R.id.main)
        intentClose.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}