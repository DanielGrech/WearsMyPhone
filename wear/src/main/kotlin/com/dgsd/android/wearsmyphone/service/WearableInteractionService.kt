package com.dgsd.android.wearsmyphone.service

import com.dgsd.android.common.WearableConstants
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

import java.util.concurrent.TimeUnit

import timber.log.Timber
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.MessageEvent
import android.os.Build

public class WearableInteractionService : WearableListenerService() {

    private var apiClient: GoogleApiClient? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")

        apiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).build()
        apiClient?.connect()
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        super.onMessageReceived(messageEvent)

        if (!apiClient!!.isConnected()) {
            val connResult = apiClient!!.blockingConnect(
                    WearableConstants.API_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            if (!connResult.isSuccess()) {
                Timber.w("Could not connect to GoogleApiClient")
                return
            }
        }

        if (WearableConstants.Path.SEND_DEVICE_NAME.equals(messageEvent?.getPath())) {
            sendDeviceName()
        }
    }

    private fun sendDeviceName() {
        val dataMapRequest = PutDataMapRequest.create(WearableConstants.Path.DEVICE_NAME)
        dataMapRequest.getDataMap().putString(WearableConstants.Data.DEVICE_NAME, getDeviceName())

        // Not used, but necessary to trigger a 'change' on Android Wear
        dataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis());

        val pendingResult = Wearable.DataApi.putDataItem(apiClient, dataMapRequest.asPutDataRequest())
        pendingResult.await(WearableConstants.API_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
    }

    private fun getDeviceName() : String {
        return "${Build.BRAND} ${Build.MODEL}"
    }
}
