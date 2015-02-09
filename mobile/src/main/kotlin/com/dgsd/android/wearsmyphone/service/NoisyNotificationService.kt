package com.dgsd.android.wearsmyphone.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import com.dgsd.android.wearsmyphone.activity.AlertActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable
import com.dgsd.android.common.WearableConstants
import java.util.concurrent.TimeUnit
import timber.log.Timber
import com.google.android.gms.wearable.PutDataMapRequest

/**
 * Service responsible for starting/stopping the ringing intended to notify the user.
 *
 * This class takes advantage of the fact requests to an {@link IntentService} are handled in
 * a FIFO queue
 */
public class NoisyNotificationService :
        IntentService(NoisyNotificationService.javaClass.getSimpleName()) {

    class object {
        private val ACTION_START : String = "_action_start"
        private val ACTION_STOP : String = "_action_stop"

        private val ACTION_ALERT_STOP : String = "_action_alert_stop"
        private val ACTION_ALERT_START : String = "_action_alert_start"

        public fun startNotify(context: Context) {
            val intent = Intent(context, javaClass.getDeclaringClass()).setAction(ACTION_START)
            context.startService(intent)
        }

        public fun stopNotify(context: Context) {
            val intent = Intent(context, javaClass.getDeclaringClass()).setAction(ACTION_STOP)
            context.startService(intent)
        }

        public fun notifyAlertChange(context: Context, isRunning: Boolean) {
            val intent = Intent(context, javaClass.getDeclaringClass())
            intent.setAction(if (isRunning) ACTION_ALERT_START else ACTION_ALERT_STOP)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent!!.getAction()) {
            ACTION_START -> startNotify()
            ACTION_STOP -> stopNotify()
            ACTION_ALERT_START -> notifyNodes(true)
            ACTION_ALERT_STOP-> notifyNodes(false)
        }
    }

    private fun startNotify() {
        val intent = Intent(getApplication(), javaClass<AlertActivity>())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication().startActivity(intent)
    }

    private fun stopNotify() {
        getApplication().sendBroadcast(Intent(AlertActivity.ACTION_STOP_NOTIFY))

    }

    private fun notifyNodes(isRunning: Boolean) {
        val apiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).build()
        val connResult = apiClient.blockingConnect(
                WearableConstants.API_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        if (!connResult.isSuccess()) {
            Timber.w("Couldn't connect to GoogleApiClient")
        } else {
            val status = if (isRunning) {
                WearableConstants.AlertStatus.RUNNING
            } else {
                WearableConstants.AlertStatus.NOT_RUNNING
            }

            val request = PutDataMapRequest.create(WearableConstants.Path.ALERT_STATUS)
            request.getDataMap().putString(WearableConstants.Data.STATUS, status)

            // Not used, but necessary to trigger a 'change' on Android Wear
            request.getDataMap().putLong("timestamp", System.currentTimeMillis())

            val pendingResult = Wearable.DataApi.putDataItem(apiClient, request.asPutDataRequest())
            pendingResult.await(WearableConstants.API_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        }

    }
}