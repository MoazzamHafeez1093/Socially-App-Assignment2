package com.example.assignment1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.adapters.MessageAdapter
import com.example.assignment1.utils.ChatMessage
import com.example.assignment1.utils.ChatRepository

class chat : AppCompatActivity() {
    private val chatRepository = ChatRepository()
    private val chatId = "default_chat" // In real app, this would be dynamic
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var selectedMessage: ChatMessage? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                sendImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupMessagesRecyclerView()
        loadMessages()
    }

    private fun setupUI() {
        // Retrieve and set the username to the TextView
        val personName = intent.getStringExtra("PersonName")
        val personNameTextView = findViewById<TextView>(R.id.personNameTextView)
        personNameTextView.text = personName

        // Set up send button
        val sendButton = findViewById<ImageButton>(R.id.sendButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        
        sendButton?.setOnClickListener {
            val message = messageInput?.text?.toString()?.trim()
            if (!message.isNullOrEmpty()) {
                sendTextMessage(message)
                messageInput?.setText("")
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up gallery button
        val galleryButton = findViewById<ImageButton>(R.id.gallery)
        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        // Set up camera button
        val cameraButton = findViewById<ImageButton>(R.id.btnCamera)
        cameraButton.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            imagePickerLauncher.launch(intent)
        }

        val callBtn = findViewById<ImageButton>(R.id.callBtn)
        callBtn.setOnClickListener {
            val intentCall = Intent(this, call::class.java)
            startActivity(intentCall)
        }
        
        // Add call buttons to chat
        val videoCallBtn = findViewById<ImageButton>(R.id.videoCallBtn)
        videoCallBtn?.setOnClickListener {
            startCall("video")
        }
        
        val voiceCallBtn = findViewById<ImageButton>(R.id.voiceCallBtn)
        voiceCallBtn?.setOnClickListener {
            startCall("voice")
        }
    }

    private fun setupMessagesRecyclerView() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageAdapter = MessageAdapter(messages) { message ->
            selectedMessage = message
            showMessageOptionsDialog()
        }
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter
    }

    private fun sendTextMessage(text: String) {
        chatRepository.sendText(chatId, text) { success ->
            if (success) {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendImage(imageUri: Uri) {
        chatRepository.sendImage(this, chatId, imageUri) { success ->
            if (success) {
                Toast.makeText(this, "Image sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages() {
        // In a real app, you'd load messages from Firebase
        // For now, we'll add some sample messages
        val sampleMessage = ChatMessage(
            messageId = "1",
            chatId = chatId,
            senderId = "other_user",
            type = "text",
            content = "Hello! How are you?",
            timestamp = System.currentTimeMillis() - 300000
        )
        messages.add(sampleMessage)
        messageAdapter.notifyDataSetChanged()
    }

    private fun showMessageOptionsDialog() {
        val message = selectedMessage ?: return
        val canEdit = System.currentTimeMillis() <= message.editableUntil

        val options = mutableListOf<String>()
        if (canEdit) {
            options.add("Edit")
            options.add("Delete")
        }
        options.add("Copy")

        if (options.isEmpty()) return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Message Options")
        builder.setItems(options.toTypedArray()) { _, which ->
            when (options[which]) {
                "Edit" -> editMessage(message)
                "Delete" -> deleteMessage(message)
                "Copy" -> copyMessage(message)
            }
        }
        builder.show()
    }

    private fun editMessage(message: ChatMessage) {
        val editText = EditText(this)
        editText.setText(message.content)
        editText.setSelection(message.content.length)

        AlertDialog.Builder(this)
            .setTitle("Edit Message")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    chatRepository.editMessage(chatId, message.messageId, newText) { success ->
                        if (success) {
                            Toast.makeText(this, "Message edited", Toast.LENGTH_SHORT).show()
                            loadMessages() // Reload messages
                        } else {
                            Toast.makeText(this, "Failed to edit message", Toast.LENGTH_SHORT).show()
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
                    if (success) {
                        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
                        loadMessages() // Reload messages
                    } else {
                        Toast.makeText(this, "Failed to delete message", Toast.LENGTH_SHORT).show()
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
        val channelName = generateChannelName()
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra("channelName", channelName)
            putExtra("callType", callType)
            putExtra("isIncomingCall", false)
        }
        startActivity(intent)
    }
    
    private fun generateChannelName(): String {
        return "socially_call_${System.currentTimeMillis()}"
    }
}