package com.dgsd.android.wearsmyphone;

import android.app.Application
import timber.log.Timber
import com.dgsd.android.wearsmyphone.util.CrashlyticsLogger
import android.os.StrictMode
import com.dgsd.android.wearsmyphone.util.ActivityCounter
import com.dgsd.android.wearsmyphone.activity.AlertActivity

class WmpApp : Application() {

    private val activityCounter = ActivityCounter()

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(activityCounter)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build())
        } else {
            Timber.plant(CrashlyticsLogger(this))
        }
    }

    public fun isAlertShowing(): Boolean {
        return activityCounter.getCurrentActivityClass()?.equals(AlertActivity.javaClass) ?: false
    }
}
