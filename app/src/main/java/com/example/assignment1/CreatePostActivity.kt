package com.example.assignment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment1.utils.PostRepository
import com.google.firebase.auth.FirebaseAuth

class CreatePostActivity : AppCompatActivity() {
    
    private lateinit var postRepository: PostRepository
    private var selectedImageUri: Uri? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val imagePreview = findViewById<ImageView>(R.id.imagePreview)
            imagePreview.setImageURI(it)
            imagePreview.visibility = ImageView.VISIBLE
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        postRepository = PostRepository()
        
        setupUI()
    }
    
    private fun setupUI() {
        val selectImageBtn = findViewById<Button>(R.id.selectImageBtn)
        val captionInput = findViewById<EditText>(R.id.captionInput)
        val createPostBtn = findViewById<Button>(R.id.createPostBtn)
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        
        selectImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        
        createPostBtn.setOnClickListener {
            val caption = captionInput.text.toString().trim()
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (caption.isEmpty()) {
                Toast.makeText(this, "Please enter a caption", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            createPostBtn.isEnabled = false
            createPostBtn.text = "Creating..."
            
            postRepository.createPost(this, selectedImageUri!!, caption) { success, postId ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent()
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show()
                        createPostBtn.isEnabled = true
                        createPostBtn.text = "Create Post"
                    }
                }
            }
        }
        
        backBtn.setOnClickListener {
            finish()
        }
    }
}