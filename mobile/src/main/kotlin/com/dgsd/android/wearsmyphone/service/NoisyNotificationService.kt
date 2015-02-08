package com.dgsd.android.wearsmyphone.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import com.dgsd.android.wearsmyphone.activity.AlertActivity
import timber.log.Timber

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

        public fun startNotify(context: Context) {
            val intent = Intent(context, javaClass.getDeclaringClass()).setAction(ACTION_START)
            context.startService(intent)
        }

        public fun stopNotify(context: Context) {
            val intent = Intent(context, javaClass.getDeclaringClass()).setAction(ACTION_STOP)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent!!.getAction()) {
            ACTION_START -> startNotify()
            ACTION_STOP -> stopNotify()
        }
    }

    private fun startNotify() {
        val intent = Intent(getApplication(), javaClass<AlertActivity>())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication().startActivity(intent)
    }

    private fun stopNotify() {
        // TODO: Stop ringing!
        getApplication().sendBroadcast(Intent(AlertActivity.ACTION_STOP_NOTIFY))
    }
}