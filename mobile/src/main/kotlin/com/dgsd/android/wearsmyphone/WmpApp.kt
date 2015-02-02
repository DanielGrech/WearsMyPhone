package com.dgsd.android.wearsmyphone;

import android.app.Application
import timber.log.Timber
import com.dgsd.android.wearsmyphone.util.CrashlyticsLogger

class WmpApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogger(this))
        }
    }
}
