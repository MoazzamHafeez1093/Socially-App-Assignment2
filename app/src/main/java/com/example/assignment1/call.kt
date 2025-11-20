package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment1.data.prefs.SessionManager

class call : AppCompatActivity() {
    
    private lateinit var callStatusText: TextView
    private lateinit var sessionManager: SessionManager
    private var receiverUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)
        receiverUserId = intent.getStringExtra("userId")

        initializeViews()
        startCall("video")
    }

    private fun initializeViews() {
        callStatusText = findViewById(R.id.callStatusText)
    }

    private fun startCall(callType: String) {
        val currentUserId = sessionManager.getUserId() ?: return
        val channelName = generateChannelName(currentUserId)
        
        // Start Agora call activity (Agora is allowed)
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra("channelName", channelName)
            putExtra("callType", callType)
            putExtra("isIncomingCall", false)
            putExtra("receiverId", receiverUserId)
        }
        startActivity(intent)
        Toast.makeText(this, "Starting call...", Toast.LENGTH_SHORT).show()
    }

    private fun generateChannelName(userId: String): String {
        val timestamp = System.currentTimeMillis()
        return "socially_${userId}_${timestamp}"
    }
}