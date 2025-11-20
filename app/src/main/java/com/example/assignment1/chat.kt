package com.example.assignment1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.adapters.MessageAdapter
import com.example.assignment1.data.network.ApiClient
import com.example.assignment1.data.prefs.SessionManager
import com.example.assignment1.data.local.AppDatabase
import com.example.assignment1.utils.ChatMessage
import com.example.assignment1.utils.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Instagram-style Chat Activity
 * Features:
 * - Send text messages
 * - Send images
 * - Share posts
 * - Edit messages (within 5 minutes)
 * - Delete messages (within 5 minutes)
 * - Real-time message updates
 */
class chat : AppCompatActivity() {
    private lateinit var chatRepository: ChatRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase
    private lateinit var chatId: String
    private lateinit var otherUserId: String
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private val PERMISSION_REQUEST_CODE = 102
    private val pollingHandler = Handler(Looper.getMainLooper())
    private val pollingInterval = 3000L // Poll every 3 seconds
    
    // Image picker
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sendImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)
        database = AppDatabase.getInstance(this)
        chatRepository = ChatRepository(this)

        // Get chat ID from intent or generate one
        otherUserId = intent.getStringExtra("userId") ?: "default_user"
        val currentUserId = sessionManager.getUserId() ?: "unknown"
        chatId = generateChatId(currentUserId, otherUserId)
        
        setupUI()
        setupMessagesRecyclerView()
        loadMessagesRealTime()
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        // Consistent chat ID regardless of who initiates
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }

    private fun setupUI() {
        // Set person name
        val personName = intent.getStringExtra("PersonName") ?: "Chat"
        val personNameTextView = findViewById<TextView>(R.id.personNameTextView)
        personNameTextView.text = personName

        // Message input
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendTextMessage(message)
                messageInput.setText("")
            }
        }

        // Gallery button (send images)
        val galleryButton = findViewById<ImageButton>(R.id.gallery)
        galleryButton.setOnClickListener {
            if (checkPermissions()) {
                imagePickerLauncher.launch("image/*")
            } else {
                requestPermissions()
            }
        }

        // Camera button
        val cameraButton = findViewById<ImageView>(R.id.btnCamera)
        cameraButton.setOnClickListener {
            if (checkPermissions()) {
                imagePickerLauncher.launch("image/*")
            } else {
                requestPermissions()
            }
        }
        
        // Share post button (new feature)
        val sharePostButton = findViewById<ImageButton>(R.id.sharePostButton)
        sharePostButton?.setOnClickListener {
            showSharePostDialog()
        }

        // Call buttons
        val callBtn = findViewById<ImageButton>(R.id.callBtn)
        callBtn?.setOnClickListener {
            startCall("voice")
        }
        
        val videoCallBtn = findViewById<ImageButton>(R.id.videoCallBtn)
        videoCallBtn?.setOnClickListener {
            startCall("video")
        }
    }
    
    private fun checkPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun setupMessagesRecyclerView() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageAdapter = MessageAdapter(messages) { message ->
            showMessageOptionsDialog(message)
        }
        messagesRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Start from bottom
        }
        messagesRecyclerView.adapter = messageAdapter
    }

    private fun loadMessagesRealTime() {
        // Initial load from Room cache
        lifecycleScope.launch {
            val cachedMessages = withContext(Dispatchers.IO) {
                database.messageDao().getConversationMessages(chatId)
            }
            messages.clear()
            messages.addAll(cachedMessages.map { it.toChatMessage() })
            messageAdapter.notifyDataSetChanged()
            if (messages.isNotEmpty()) {
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
        
        // Start polling for new messages
        startPollingMessages()
    }
    
    private fun startPollingMessages() {
        pollingHandler.postDelayed(object : Runnable {
            override fun run() {
                fetchMessages()
                pollingHandler.postDelayed(this, pollingInterval)
            }
        }, pollingInterval)
    }
    
    private fun fetchMessages() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.getApiService(this@chat)
                val response = withContext(Dispatchers.IO) {
                    apiService.getConversation(otherUserId.toInt())
                }
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val fetchedMessages = response.body()?.data ?: emptyList()
                    
                    // Cache in Room
                    withContext(Dispatchers.IO) {
                        fetchedMessages.forEach { message ->
                            database.messageDao().insertMessage(
                                com.example.assignment1.data.local.entities.MessageEntity.fromMessage(message)
                            )
                        }
                    }
                    
                    // Update UI
                    messages.clear()
                    messages.addAll(fetchedMessages.map { 
                        ChatMessage(
                            messageId = it.id.toString(),
                            chatId = chatId,
                            senderId = it.sender_id.toString(),
                            type = it.type ?: "text",
                            content = it.message ?: it.media_url ?: "",
                            timestamp = it.created_at?.let { dateStr ->
                                // Parse ISO date to timestamp
                                try {
                                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                                        .parse(dateStr)?.time ?: System.currentTimeMillis()
                                } catch (e: Exception) {
                                    System.currentTimeMillis()
                                }
                            } ?: System.currentTimeMillis(),
                            isVanishMode = it.vanish_mode == 1
                        )
                    })
                    messageAdapter.notifyDataSetChanged()
                    if (messages.isNotEmpty()) {
                        messagesRecyclerView.scrollToPosition(messages.size - 1)
                    }
                }
            } catch (e: Exception) {
                // Silent fail - use cached messages
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        pollingHandler.removeCallbacksAndMessages(null)
    }

    private fun sendTextMessage(text: String) {
        // Get vanish mode toggle state if exists
        val vanishModeToggle = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.vanishModeToggle)
        val isVanishMode = vanishModeToggle?.isChecked ?: false
        
        chatRepository.sendText(chatId, text, isVanishMode) { success ->
            runOnUiThread {
                if (!success) {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                } else {
                    // Send FCM notification to the other user
                    val currentUserName = sessionManager.getUsername() 
                        ?: intent.getStringExtra("PersonName") 
                        ?: "Someone"
                    
                    sendNotificationToUser(
                        userId = otherUserId,
                        title = currentUserName,
                        body = text,
                        type = "new_message"
                    )
                    
                    // Immediately fetch to show sent message
                    fetchMessages()
                }
            }
        }
    }

    private fun sendImage(imageUri: Uri) {
        Toast.makeText(this, "Sending image...", Toast.LENGTH_SHORT).show()
        
        // Get vanish mode state
        val vanishModeToggle = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.vanishModeToggle)
        val isVanishMode = vanishModeToggle?.isChecked ?: false
        
        chatRepository.sendImage(this, chatId, imageUri, isVanishMode) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Image sent", Toast.LENGTH_SHORT).show()
                    fetchMessages()
                } else {
                    Toast.makeText(this, "Failed to send image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun showSharePostDialog() {
        // Show dialog to select a post to share
        AlertDialog.Builder(this)
            .setTitle("Share Post")
            .setMessage("Post sharing feature - select from your recent posts")
            .setPositiveButton("Select") { _, _ ->
                // In a real app, show a list of posts to select from
                val postId = "sample_post_id"
                sharePost(postId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun sharePost(postId: String) {
        chatRepository.sendPost(chatId, postId) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Post shared", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to share post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMessageOptionsDialog(message: ChatMessage) {
        val currentUserId = sessionManager.getUserId() ?: return
        
        // Only allow editing/deleting own messages
        if (message.senderId != currentUserId) {
            return
        }
        
        val currentTime = System.currentTimeMillis()
        val timeSinceMessage = currentTime - message.timestamp
        val fiveMinutes = 5 * 60 * 1000L
        
        // Check if within 5 minutes
        val canEdit = timeSinceMessage <= fiveMinutes && message.type == "text"
        val canDelete = timeSinceMessage <= fiveMinutes

        val options = mutableListOf<String>()
        if (canEdit) options.add("Edit")
        if (canDelete) options.add("Delete")
        if (message.type == "text") options.add("Copy")

        if (options.isEmpty()) {
            Toast.makeText(this, "No actions available (5 minute limit expired)", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Message Options")
            .setItems(options.toTypedArray()) { _, which ->
                when (options[which]) {
                    "Edit" -> editMessage(message)
                    "Delete" -> deleteMessage(message)
                    "Copy" -> copyMessage(message)
                }
            }
            .show()
    }

    private fun editMessage(message: ChatMessage) {
        val editText = EditText(this).apply {
            setText(message.content)
            setSelection(message.content.length)
            setPadding(20, 20, 20, 20)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Message")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    chatRepository.editMessage(chatId, message.messageId, newText) { success ->
                        runOnUiThread {
                            if (success) {
                                Toast.makeText(this, "Message edited", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to edit (5 minute limit)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMessage(message: ChatMessage) {
        AlertDialog.Builder(this)
            .setTitle("Delete Message")
            .setMessage("Are you sure you want to delete this message?")
            .setPositiveButton("Delete") { _, _ ->
                chatRepository.deleteMessage(chatId, message.messageId) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to delete (5 minute limit)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun copyMessage(message: ChatMessage) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Message", message.content)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Message copied", Toast.LENGTH_SHORT).show()
    }
    
    private fun startCall(callType: String) {
        val channelName = "call_${System.currentTimeMillis()}"
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra("channelName", channelName)
            putExtra("callType", callType)
            putExtra("isIncomingCall", false)
        }
        startActivity(intent)
    }
    
    private fun sendNotificationToUser(userId: String, title: String, body: String, type: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.getApiService(this@chat)
                val payload = mapOf(
                    "userId" to userId.toInt(),
                    "title" to title,
                    "body" to body,
                    "type" to type
                )
                
                withContext(Dispatchers.IO) {
                    apiService.sendFCMNotification(payload)
                }
                
                android.util.Log.d("FCM", "Notification sent via API to user: $userId")
            } catch (e: Exception) {
                android.util.Log.e("FCM", "Error sending notification: ${e.message}")
            }
        }
    }
}
