package com.dgsd.android.wearsmyphone.service

import com.google.android.gms.wearable.MessageEvent

import timber.log.Timber
import com.dgsd.android.common.WearableConstants
import com.dgsd.android.common.service.BaseWearableInteractionService
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.dgsd.android.wearsmyphone.WmpApp

public class WearableInteractionService : BaseWearableInteractionService() {

    override fun onMessageEvent(client: GoogleApiClient, event: MessageEvent) {
        when (event.getPath()) {
            WearableConstants.Path.ALERT_START -> { NoisyNotificationService.startNotify(this) }
            WearableConstants.Path.ALERT_STOP -> { NoisyNotificationService.stopNotify(this) }
            WearableConstants.Path.SEND_ALERT_STATUS -> { sendAlertStatus() }
            WearableConstants.Path.SEND_DEVICE_NAME -> { sendDeviceName() }
            else -> {
                Timber.w("Unrecognised message event path: %s", event.getPath())
            }
        }
    }

    private fun sendDeviceName() {
        val dataMapRequest = PutDataMapRequest.create(WearableConstants.Path.DEVICE_NAME)
        dataMapRequest.getDataMap().putString(WearableConstants.Data.DEVICE_NAME, getDeviceName())

        sendData(dataMapRequest)
    }

    private fun sendAlertStatus() {
        val app = getApplication() as WmpApp

        val status = if (app.isAlertShowing()) {
            WearableConstants.AlertStatus.RUNNING
        } else {
            WearableConstants.AlertStatus.NOT_RUNNING
        }

        val dataMapRequest = PutDataMapRequest.create(WearableConstants.Path.ALERT_STATUS)
        dataMapRequest.getDataMap().putString(WearableConstants.Data.STATUS, status)

        sendData(dataMapRequest)
    }
}
