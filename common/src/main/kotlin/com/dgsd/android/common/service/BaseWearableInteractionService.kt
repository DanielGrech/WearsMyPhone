package com.dgsd.android.common.service

import com.google.android.gms.wearable.WearableListenerService
import com.google.android.gms.common.api.GoogleApiClient
import timber.log.Timber
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.PutDataMapRequest
import com.dgsd.android.common.WearableConstants
import java.util.concurrent.TimeUnit
import android.os.Build
import com.google.android.gms.wearable.MessageEvent

public abstract class BaseWearableInteractionService : WearableListenerService() {

    protected abstract fun onMessageEvent(client: GoogleApiClient, event: MessageEvent)

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
                    WearableConstants.API_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            if (!connResult.isSuccess()) {
                Timber.w("Could not connect to GoogleApiClient")
                return
            }
        }

        when (messageEvent?.getPath()) {
            else -> { onMessageEvent(apiClient!!, messageEvent!!) }
        }
    }

    protected fun sendData(dataMapRequest: PutDataMapRequest) {
        // Not used, but necessary to trigger a 'change' on Android Wear
        dataMapRequest.getDataMap().putLong("timestamp", System.currentTimeMillis())

        val pendingResult = Wearable.DataApi.putDataItem(apiClient, dataMapRequest.asPutDataRequest())
        pendingResult.await(WearableConstants.API_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
    }

    protected fun getDeviceName() : String {
        return "${Build.BRAND} ${Build.MODEL}"
    }
}