package com.example.assignment1.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FollowManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun sendFollowRequest(targetUserId: String, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onComplete(false)
        val reqId = db.child("followRequests").child(targetUserId).push().key ?: return onComplete(false)
        val payload = mapOf(
            "requestId" to reqId,
            "fromUserId" to uid,
            "toUserId" to targetUserId,
            "timestamp" to System.currentTimeMillis()
        )
        db.child("followRequests").child(targetUserId).child(reqId).setValue(payload)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun acceptFollowRequest(requestId: String, fromUserId: String, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onComplete(false)
        val updates = hashMapOf<String, Any?>()
        updates["followers/$uid/$fromUserId"] = true
        updates["following/$fromUserId/$uid"] = true
        updates["followRequests/$uid/$requestId"] = null
        db.updateChildren(updates).addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    fun rejectFollowRequest(requestId: String, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onComplete(false)
        db.child("followRequests").child(uid).child(requestId).removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}


