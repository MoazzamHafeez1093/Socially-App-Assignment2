package com.example.assignment1.models

data class Post(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: MutableList<String> = mutableListOf(),
    val comments: MutableList<Comment> = mutableListOf()
)

data class Comment(
    val commentId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
