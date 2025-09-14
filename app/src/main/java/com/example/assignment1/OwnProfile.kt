package com.example.assignment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OwnProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_own_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the profile image URI from the intent
        val imageUriString = intent.getStringExtra("PROFILE_IMAGE_URI")
        val profileImageView = findViewById<ImageView>(R.id.UserStoryView)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            profileImageView.setImageURI(imageUri)
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

        // Set up the Search button to open the search screen
        val searchBtn = findViewById<ImageButton>(R.id.tab_2_search)
        searchBtn.setOnClickListener {
            val intentSearch = Intent(this, search::class.java)
            startActivity(intentSearch)
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


    }
}