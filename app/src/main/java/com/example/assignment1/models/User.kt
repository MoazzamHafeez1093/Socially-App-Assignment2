package com.example.assignment1.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val followers: MutableList<String> = mutableListOf(),
    val following: MutableList<String> = mutableListOf(),
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)
