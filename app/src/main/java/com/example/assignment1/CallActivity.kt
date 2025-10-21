package com.example.assignment1

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CallActivity : AppCompatActivity() {
    
    private lateinit var localVideoView: ImageView
    private lateinit var remoteVideoView: ImageView
    private lateinit var callStatusText: TextView
    private lateinit var endCallBtn: ImageButton
    private lateinit var muteBtn: ImageButton
    private lateinit var videoBtn: ImageButton
    private lateinit var speakerBtn: ImageButton
    
    private var channelName: String = ""
    private var callType: String = "voice"
    private var isIncomingCall: Boolean = false
    private var isMuted: Boolean = false
    private var isVideoEnabled: Boolean = true
    private var isSpeakerOn: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Get intent extras
        channelName = intent.getStringExtra("channelName") ?: "default_channel"
        callType = intent.getStringExtra("callType") ?: "voice"
        isIncomingCall = intent.getBooleanExtra("isIncomingCall", false)
        
        initializeViews()
        setupClickListeners()
        
        // Simulate call connection
        simulateCallConnection()
    }
    
    private fun initializeViews() {
        localVideoView = findViewById(R.id.localVideoView)
        remoteVideoView = findViewById(R.id.remoteVideoView)
        callStatusText = findViewById(R.id.callStatusText)
        endCallBtn = findViewById(R.id.callEnd)
        // For now, use placeholder buttons since the layout doesn't have these IDs
        // muteBtn = findViewById(R.id.muteBtn)
        // videoBtn = findViewById(R.id.videoBtn)
        // speakerBtn = findViewById(R.id.speakerBtn)
        
        // Set initial status
        callStatusText.text = if (isIncomingCall) "Incoming call..." else "Calling..."
        
        // Show/hide video views based on call type
        if (callType == "voice") {
            localVideoView.visibility = View.GONE
            remoteVideoView.visibility = View.GONE
            // videoBtn.visibility = View.GONE
        }
    }
    
    private fun setupClickListeners() {
        endCallBtn.setOnClickListener {
            endCall()
        }
        
        // For now, disable these buttons since they're not in the layout
        // muteBtn.setOnClickListener {
        //     toggleMute()
        // }
        
        // videoBtn.setOnClickListener {
        //     toggleVideo()
        // }
        
        // speakerBtn.setOnClickListener {
        //     toggleSpeaker()
        // }
    }
    
    private fun simulateCallConnection() {
        // Simulate call connection after 2 seconds
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            callStatusText.text = "Connected to call"
            if (callType == "video") {
                remoteVideoView.visibility = View.VISIBLE
                localVideoView.visibility = View.VISIBLE
            }
        }, 2000)
    }
    
    private fun endCall() {
        finish()
    }
    
    private fun toggleMute() {
        isMuted = !isMuted
        muteBtn.setImageResource(if (isMuted) R.drawable.mic_off else R.drawable.mic_on)
        Toast.makeText(this, if (isMuted) "Muted" else "Unmuted", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleVideo() {
        if (callType == "video") {
            isVideoEnabled = !isVideoEnabled
            videoBtn.setImageResource(if (isVideoEnabled) R.drawable.video_on else R.drawable.video_off)
            localVideoView.visibility = if (isVideoEnabled) View.VISIBLE else View.GONE
            Toast.makeText(this, if (isVideoEnabled) "Video on" else "Video off", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun toggleSpeaker() {
        isSpeakerOn = !isSpeakerOn
        speakerBtn.setImageResource(if (isSpeakerOn) R.drawable.speaker_on else R.drawable.speaker_off)
        Toast.makeText(this, if (isSpeakerOn) "Speaker on" else "Speaker off", Toast.LENGTH_SHORT).show()
    }
}