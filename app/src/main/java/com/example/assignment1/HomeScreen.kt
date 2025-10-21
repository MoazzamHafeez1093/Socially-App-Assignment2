package com.example.assignment1

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen)

        // Set padding for the main layout based on system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the profile image URI from the intent
        val imageUriString = intent.getStringExtra("PROFILE_IMAGE_URI")
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val profileImageInFeed = findViewById<ImageButton>(R.id.tab_5)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            profileImageView.setImageURI(imageUri)
            profileImageInFeed.setImageURI(imageUri)
        } else {
            // 👇 show fallback placeholder
            profileImageView.setImageResource(R.drawable.ic_default_profile)
            profileImageInFeed.setImageResource(R.drawable.ic_default_profile)
        }


        // Retrieve and set the username to the TextView
        val username = intent.getStringExtra("USERNAME_KEY")
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        usernameTextView.text = username

        // Set up the Search button to open the search screen
        val searchBtn = findViewById<ImageButton>(R.id.tab_2_search)
        searchBtn.setOnClickListener {
            val intentSearch = Intent(this, search::class.java)
            imageUriString?.let {
                val imageUri = Uri.parse(it)
                intentSearch.putExtra("PROFILE_IMAGE_URI", imageUri.toString()) // Pass URI as String
            }
            startActivity(intentSearch)
            overridePendingTransition(0, 0)
        }

        // Set up the Share button to open the message list screen
        val shareBtn = findViewById<ImageButton>(R.id.shareButton)
        shareBtn.setOnClickListener {
            val intentShare = Intent(this, messageList::class.java)
            startActivity(intentShare)
        }

        val storyOwnBtn = findViewById<ImageView>(R.id.profileImageView)
        storyOwnBtn.setOnClickListener {
            val intentStoryOwn = Intent(this, storyViewOwn::class.java)
            imageUriString?.let {
                val imageUri = Uri.parse(it)
                intentStoryOwn.putExtra("PROFILE_IMAGE_URI", imageUri.toString()) // Pass URI as String
            }
            startActivity(intentStoryOwn)
            overridePendingTransition(0, 0)
        }

        val UserStoryBtn = findViewById<ImageView>(R.id.UserStoryView)
        UserStoryBtn.setOnClickListener {
            val intentUserStory = Intent(this, UserStoryView::class.java)
            startActivity(intentUserStory)
            overridePendingTransition(0, 0)
        }

        val notificationBtn = findViewById<ImageButton>(R.id.tab_4_notification)
        notificationBtn.setOnClickListener {
            val intentnotification = Intent(this, notifications::class.java)
            startActivity(intentnotification)
            overridePendingTransition(0, 0)
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
        }

    }
}