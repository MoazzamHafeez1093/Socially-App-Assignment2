package com.example.assignment1

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IAgoraEventHandler
import io.agora.rtc2.RtcEngine

class call : AppCompatActivity() {
    private var rtcEngine: RtcEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentCall = findViewById<ImageButton>(R.id.callEnd)
        intentCall.setOnClickListener {
            leaveChannel()
            finish()
        }

        initAgoraAndJoin()
    }

    private fun initAgoraAndJoin() {
        val appId = BuildConfig.AGORA_APP_ID
        val token = BuildConfig.AGORA_TOKEN
        if (appId.isBlank() || token.isBlank()) {
            Toast.makeText(this, "Agora not configured", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            rtcEngine = RtcEngine.create(this, appId, object : IAgoraEventHandler() {})
        } catch (e: Exception) {
            Toast.makeText(this, "Agora init failed", Toast.LENGTH_SHORT).show()
            return
        }
        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        }
        rtcEngine?.joinChannel(token, "socially_room", 0, options)
    }

    private fun leaveChannel() {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
    }
}