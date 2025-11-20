package com.example.assignment1.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Online status management moved to REST API via PresenceManager
 * This is a stub for compatibility
 */
class OnlineStatusManager : Application.ActivityLifecycleCallbacks {
    private var currentUserId: String? = null

    fun setCurrentUser(userId: String) {
        currentUserId = userId
        // Presence managed by PresenceManager via REST API
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    fun getUserOnlineStatus(userId: String, onComplete: (Boolean, Long) -> Unit) {
        // Stub - return offline
        onComplete(false, 0L)
    }
}
