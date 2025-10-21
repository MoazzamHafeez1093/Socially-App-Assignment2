package com.example.assignment1.models

import java.io.Serializable

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val isOnline: Boolean = false
) : Serializable