package com.dgsd.android.wearsmyphone.service

import com.dgsd.android.common.service.BaseWearableInteractionService
import com.google.android.gms.wearable.MessageEvent
import timber.log.Timber
import com.google.android.gms.common.api.GoogleApiClient
import com.dgsd.android.common.WearableConstants
import com.google.android.gms.wearable.PutDataMapRequest

public class WearableInteractionService : BaseWearableInteractionService() {

    override fun onMessageEvent(client: GoogleApiClient, event: MessageEvent) {
        when (event.getPath()) {
            WearableConstants.Path.SEND_DEVICE_NAME -> { sendDeviceName() }
            else -> {
                Timber.w("Unrecognised message event path: %s", event.getPath())
            }
        }
    }

    private fun sendDeviceName() {
        val dataMapRequest = PutDataMapRequest.create(WearableConstants.Path.WATCH_NAME)
        dataMapRequest.getDataMap().putString(WearableConstants.Data.DEVICE_NAME, getDeviceName())

        sendData(dataMapRequest)
    }

}
