package com.dgsd.android.wearsmyphone.service

import com.dgsd.android.common.WearableConstants
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

import java.util.concurrent.TimeUnit

import timber.log.Timber

public class WearableInteractionService : WearableListenerService() {

    private var apiClient: GoogleApiClient? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")

        apiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).build()
        apiClient?.connect()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (!apiClient!!.isConnected()) {
            val connResult = apiClient!!.blockingConnect(
                    WearableConstants.API_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            if (!connResult.isSuccess()) {
                Timber.w("Could not connect to GoogleApiClient")
                return
            }
        }

        when (messageEvent.getPath()) {
            WearableConstants.Path.ALERT_START -> { NoisyNotificationService.startNotify(this) }
            WearableConstants.Path.ALERT_STOP -> { NoisyNotificationService.stopNotify(this) }
            else -> {
                Timber.w("Unrecognised message event path: %s", messageEvent.getPath())
            }
        }
    }
}
