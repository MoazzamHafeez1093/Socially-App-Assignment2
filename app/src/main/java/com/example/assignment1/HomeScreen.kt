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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.adapters.PostAdapter
import com.example.assignment1.models.Post
import com.example.assignment1.utils.Base64Image
import com.example.assignment1.utils.PostRepository
import com.example.assignment1.utils.PresenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeScreen : AppCompatActivity() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var postRepository: PostRepository
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen)
        PresenceManager.setOnline()
        
        // Initialize post repository and adapter
        postRepository = PostRepository()
        setupPostsRecyclerView()

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
            val intentStoryUpload = Intent(this, story_Upload::class.java)
            imageUriString?.let {
                val imageUri = Uri.parse(it)
                intentStoryUpload.putExtra("PROFILE_IMAGE_URI", imageUri.toString()) // Pass URI as String
            }
            startActivity(intentStoryUpload)
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

        // Set up the Plus button to open create post screen
        val plusBtn = findViewById<ImageButton>(R.id.tab_3_plus)
        plusBtn.setOnClickListener {
            val intentCreatePost = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intentCreatePost, 100)
        }

        // Load posts from Firebase
        loadPostsFromFirebase()
    }

    private fun setupPostsRecyclerView() {
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        postAdapter = PostAdapter(posts) { post ->
            // Handle comment click
            val intentComments = Intent(this, CommentsActivity::class.java)
            intentComments.putExtra("post", post)
            startActivity(intentComments)
        }
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        postsRecyclerView.adapter = postAdapter
    }

    private fun loadPostsFromFirebase() {
        postRepository.getPosts { loadedPosts ->
            runOnUiThread {
                posts.clear()
                posts.addAll(loadedPosts)
                postAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh posts when returning from create post
            loadPostsFromFirebase()
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
        val currentTime = System.currentTimeMillis()
        
        // Load stories from Firebase with 24-hour expiry
        database.reference.child("stories")
            .orderByChild("expiresAt")
            .startAt(currentTime.toDouble()) // Only get stories that haven't expired
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stories = mutableListOf<Map<String, Any>>()
                    for (storySnapshot in snapshot.children) {
                        val storyData = storySnapshot.getValue(Map::class.java) as? Map<String, Any>
                        storyData?.let { stories.add(it) }
                    }
                    
                    // Update UI with Firebase stories
                    updateStoriesUI(stories)
                    
                    // Clean up expired stories
                    cleanupExpiredStories(currentTime)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeScreen, "Failed to load stories", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
    private fun updateStoriesUI(stories: List<Map<String, Any>>) {
        // Find the stories container in the layout - use a placeholder for now
        val storiesContainer = findViewById<LinearLayout>(R.id.main)
        
        if (storiesContainer != null && stories.isNotEmpty()) {
            // Clear existing story views
            storiesContainer.removeAllViews()
            
            // Add Firebase stories to the container
            stories.forEach { storyData ->
                val storyView = layoutInflater.inflate(R.layout.story_item_plain, storiesContainer, false)
                
                val storyImageView = storyView.findViewById<ImageView>(R.id.profileImageView)
                val storyUsername = storyView.findViewById<TextView>(R.id.usernameTextView)
                
                // Set username
                val username = storyData["username"] as? String ?: "User"
                storyUsername.text = username
                
                // Set story image (Base64)
                val imageBase64 = storyData["imageBase64"] as? String
                if (imageBase64 != null) {
                    try {
                        val bitmap = Base64Image.base64ToBitmap(imageBase64)
                        storyImageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        storyImageView.setImageResource(R.drawable.ic_default_profile)
                    }
                }
                
                storiesContainer.addView(storyView)
            }
            
            Toast.makeText(this, "Loaded ${stories.size} stories", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun cleanupExpiredStories(currentTime: Long) {
        // Remove expired stories from Firebase
        database.reference.child("stories")
            .orderByChild("expiresAt")
            .endAt(currentTime.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expiredStoryIds = mutableListOf<String>()
                    for (storySnapshot in snapshot.children) {
                        expiredStoryIds.add(storySnapshot.key ?: "")
                    }
                    
                    // Remove expired stories
                    expiredStoryIds.forEach { storyId ->
                        database.reference.child("stories").child(storyId).removeValue()
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error silently
                }
            })
    }
}