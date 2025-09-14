package com.example.assignment1

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView

class search : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the image URI passed from the previous activity
        val imageUriString = intent.getStringExtra("PROFILE_IMAGE_URI")

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            val imageView = findViewById<ImageView>(R.id.tab_5)
            imageView.setImageURI(imageUri) // Set the image URI to the ImageView
        }

        val homeBtn = findViewById<ImageButton>(R.id.tab_1)
        homeBtn.setOnClickListener {
            val intentHome = Intent(this, HomeScreen::class.java).apply {
                // If HomeScreen exists in the task, move it to front; else create it
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intentHome)
            overridePendingTransition(0, 0)
            finish()
        }

        val notificationBtn = findViewById<ImageButton>(R.id.tab_4_notification)
        notificationBtn.setOnClickListener {
            val intentnotification = Intent(this, notifications::class.java)
            startActivity(intentnotification)
            overridePendingTransition(0, 0)
            finish()
        }

        val MyProfileBtn = findViewById<ImageButton>(R.id.tab_5)
        MyProfileBtn.setOnClickListener {
            val intentMyProfile = Intent(this, OwnProfile::class.java)
            imageUriString?.let {
                val imageUri = Uri.parse(it)
                intentMyProfile.putExtra("PROFILE_IMAGE_URI", imageUri.toString()) // Pass URI as String
            }
            startActivity(intentMyProfile)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}