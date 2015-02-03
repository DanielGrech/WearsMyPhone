package com.dgsd.android.wearsmyphone;

import android.app.Application
import timber.log.Timber
import com.dgsd.android.wearsmyphone.util.CrashlyticsLogger
import android.os.StrictMode

class WmpApp : Application() {

    override fun onCreate() {
        super.onCreate()

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
}
