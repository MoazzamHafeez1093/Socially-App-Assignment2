package com.example.assignment1.utils

import android.content.Context
import android.widget.Toast
import com.example.assignment1.models.User

/**
 * Follow operations now handled via REST API
 * All methods are stubs for compatibility
 */
class FollowManager {
    
    // Send follow request (stub - use REST API)
    fun sendFollowRequest(fromUserId: String, toUserId: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        onComplete(false, "Use REST API followUser() method")
    }
    
    // Accept follow request (stub - use REST API)
    fun acceptFollowRequest(fromUserId: String, toUserId: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        onComplete(false, "Use REST API")
    }
    
    // Reject follow request (stub - use REST API)
    fun rejectFollowRequest(fromUserId: String, toUserId: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        onComplete(false, "Use REST API")
    }
    
    // Get followers list (stub - use REST API)
    fun getFollowers(userId: String, onComplete: (List<User>) -> Unit) {
        onComplete(emptyList())
    }
    
    // Get following list (stub - use REST API)
    fun getFollowing(userId: String, onComplete: (List<User>) -> Unit) {
        onComplete(emptyList())
    }
    
    // Get pending follow requests (stub - use REST API)
    fun getPendingFollowRequests(userId: String, onComplete: (List<User>) -> Unit) {
        onComplete(emptyList())
    }
    
    // Check if following (stub - use REST API)
    fun isFollowing(currentUserId: String, targetUserId: String, onComplete: (Boolean) -> Unit) {
        onComplete(false)
    }
    
    // Unfollow user (stub - use REST API)
    fun unfollowUser(currentUserId: String, targetUserId: String, context: Context, onComplete: (Boolean, String?) -> Unit) {
        onComplete(false, "Use REST API unfollowUser() method")
    }
}
