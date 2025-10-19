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
import android.widget.LinearLayout
import android.widget.Toast
import com.example.assignment1.utils.Base64Image
import com.example.assignment1.utils.PresenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeScreen : AppCompatActivity() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen)
        PresenceManager.setOnline()

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

        // Load and display stories from Firebase with 24-hour expiry
        loadStoriesFromFirebase()

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

    override fun onStart() {
        super.onStart()
        PresenceManager.setOnline()
    }

    override fun onStop() {
        super.onStop()
        PresenceManager.setOffline()
    }

    private fun loadStoriesFromFirebase() {
        val storiesRow = findViewById<LinearLayout>(R.id.storiesLinearLayout)
        if (storiesRow == null) {
            Toast.makeText(this, "Stories container not found", Toast.LENGTH_SHORT).show()
            return
        }

        val now = System.currentTimeMillis()
        val twentyFourHoursAgo = now - (24 * 60 * 60 * 1000)
        
        // Clean up expired stories first
        database.reference.child("stories")
            .orderByChild("expiresAt")
            .endAt(twentyFourHoursAgo.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Load active stories (not expired)
        database.reference.child("stories")
            .orderByChild("expiresAt")
            .startAt(now.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    storiesRow.removeAllViews()
                    var storyCount = 0
                    
                    if (!snapshot.exists()) {
                        // Show static stories if no Firebase stories exist
                        showStaticStories()
                        return
                    }
                    
                    for (child in snapshot.children) {
                        val base64 = child.child("imageBase64").getValue(String::class.java) ?: continue
                        val userId = child.child("userId").getValue(String::class.java) ?: ""
                        val usernameVal = child.child("username").getValue(String::class.java) ?: userId.take(6)
                        val storyId = child.key ?: continue

                        val container = layoutInflater.inflate(R.layout.story_item, storiesRow, false)
                        val img = container.findViewById<ImageView>(R.id.storyImage)
                        val name = container.findViewById<TextView>(R.id.storyUsername)
                        
                        try {
                            val bmp = Base64Image.base64ToBitmap(base64)
                            img.setImageBitmap(bmp)
                        } catch (e: Exception) {
                            img.setImageResource(R.drawable.ic_default_profile)
                        }
                        name.text = usernameVal
                        
                        // Set click listener for story
                        container.setOnClickListener {
                            val intent = Intent(this@HomeScreen, UserStoryView::class.java)
                            intent.putExtra("storyId", storyId)
                            intent.putExtra("userId", userId)
                            intent.putExtra("username", usernameVal)
                            startActivity(intent)
                        }
                        
                        storiesRow.addView(container)
                        storyCount++
                    }
                    
                    if (storyCount == 0) {
                        showStaticStories()
                    } else {
                        Toast.makeText(this@HomeScreen, "Loaded $storyCount active stories", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeScreen, "Failed to load stories: ${error.message}", Toast.LENGTH_SHORT).show()
                    showStaticStories()
                }
            })
    }

    private fun showStaticStories() {
        // Keep the existing static stories from the layout as fallback
        Toast.makeText(this, "Showing static stories", Toast.LENGTH_SHORT).show()
    }
}