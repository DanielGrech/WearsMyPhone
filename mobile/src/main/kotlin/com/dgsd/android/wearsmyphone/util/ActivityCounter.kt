package com.dgsd.android.wearsmyphone.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

public class ActivityCounter : Application.ActivityLifecycleCallbacks {

    /**
     * The number of activities the app is showing
     */
    private var activityCount = 0

    private var currentActivityCls: Class<out Activity>? = null

    /**
     * @return <code>true</code> if the app has at least 1 activity visible,
     * <code>false otherwise</code>
     */
    public fun isAppInForeground(): Boolean {
        return activityCount > 0
    }

    public fun getCurrentActivityClass(): Class<out Activity>? {
        return currentActivityCls
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++
        currentActivityCls = activity.javaClass
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount = Math.max(0, activityCount - 1)
        currentActivityCls = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}