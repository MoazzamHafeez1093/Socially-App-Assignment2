package com.example.assignment1

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import com.example.assignment1.utils.ChatRepository

class chat : AppCompatActivity() {
    private val chatRepository = ChatRepository()
    private val chatId = "default_chat" // In real app, this would be dynamic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                chatRepository.sendText(chatId, message) { success ->
                    if (success) {
                        messageInput?.setText("")
                        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        val callBtn = findViewById<ImageButton>(R.id.callBtn)
        callBtn.setOnClickListener {
            val intentCall = Intent(this, call::class.java)
            startActivity(intentCall)
        }
    }
}