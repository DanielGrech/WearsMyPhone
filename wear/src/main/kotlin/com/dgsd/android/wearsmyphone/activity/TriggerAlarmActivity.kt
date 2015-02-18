package com.dgsd.android.wearsmyphone.activity

import android.app.Activity
import android.os.Bundle

import com.dgsd.android.common.WearableConstants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.dgsd.android.common.util.getDeviceNames
import com.dgsd.android.wearsmyphone.R
import android.widget.TextView
import android.support.wearable.view.CircledImageView
import com.dgsd.android.wearsmyphone.util.onPreDraw
import com.dgsd.android.common.util.getCurrentAlertStatus
import android.support.wearable.view.BoxInsetLayout
import android.support.annotation.DrawableRes
import android.graphics.drawable.Drawable
import android.graphics.PorterDuff
import android.support.annotation.StringRes
import com.dgsd.android.wearsmyphone.util.show
import com.dgsd.android.wearsmyphone.util.hide

public class TriggerAlarmActivity : Activity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    private var apiClient: GoogleApiClient? = null

    private var insetLayout: BoxInsetLayout? = null

//    private var deviceName: TextView? = null

    private var image: CircledImageView? = null

    private var errorMessage: TextView? = null

    private var nodeId: String? = null

    private var currentAlertStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.act_trigger_alarm)

        insetLayout = findViewById(R.id.inset_layout) as BoxInsetLayout
//        deviceName = findViewById(R.id.node_name) as TextView
        errorMessage = findViewById(R.id.error_message) as TextView
        image = findViewById(R.id.action) as CircledImageView
        image?.setImageDrawable(getTintedDrawable(R.drawable.ic_action_play))
        image?.onPreDraw {
            val insets = insetLayout!!.getInsets()
            val inset = Math.max(Math.abs(insets.width()), Math.abs(insets.height()))
            val radius = image!!.getHeight().toFloat().div(2).minus(inset.times(2))

            image?.setCircleRadius(radius)
            image?.setCircleRadiusPressed(radius.times(0.95f))
        }

        image?.setOnClickListener {
            var path: String? = null
            when (currentAlertStatus) {
                WearableConstants.AlertStatus.RUNNING -> {
                    path = WearableConstants.Path.ALERT_STOP
                }
                WearableConstants.AlertStatus.NOT_RUNNING -> {
                    path = WearableConstants.Path.ALERT_START
                }
            }

            if (path != null && nodeId != null) {
                image?.showIndeterminateProgress(true)
                Wearable.MessageApi.sendMessage(apiClient, nodeId, path, null)
            }
        }

        apiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).build()
        apiClient?.registerConnectionCallbacks(this)
        apiClient?.registerConnectionFailedListener(this)
        apiClient?.connect()
    }

    override fun onDestroy() {
        Wearable.DataApi.removeListener(apiClient, this)
        if (apiClient!!.isConnected()) {
            apiClient!!.disconnect()
        }
        super<Activity>.onDestroy()
    }

    override fun onConnected(bundle: Bundle?) {
        Wearable.DataApi.addListener(apiClient, this)

        Thread {
            val nodes = Wearable.NodeApi.getConnectedNodes(apiClient).await()?.getNodes()
            if (nodes == null || nodes.isEmpty()) {
                runOnUiThread {
                    showError(R.string.error_message_no_nodes)
                }
            } else {
                nodeId = nodes.first().getId()
                Wearable.MessageApi.sendMessage(apiClient, nodeId,
                        WearableConstants.Path.SEND_DEVICE_NAME, null).await()

                Wearable.MessageApi.sendMessage(apiClient, nodeId,
                        WearableConstants.Path.SEND_ALERT_STATUS, null).await()
            }
        }.start()
    }

    override fun onConnectionSuspended(reason: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult?) {
        showError(R.string.error_message_play_services)
    }

    private fun showError(StringRes errorMessageRes: Int) {
        errorMessage?.setText(errorMessageRes)
        errorMessage?.show()
        image?.hide()
    }

    private fun updateCurrentAlertStatus(alertStatus: String) {
        image?.showIndeterminateProgress(false)
        currentAlertStatus = alertStatus
        when (alertStatus) {
            WearableConstants.AlertStatus.RUNNING -> {
                image?.setImageDrawable(getTintedDrawable(R.drawable.ic_action_stop))
            }
            WearableConstants.AlertStatus.NOT_RUNNING -> {
                image?.setImageDrawable(getTintedDrawable(R.drawable.ic_action_play))
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer?) {
        val names = dataEvents?.getDeviceNames(false)
        if (names != null && !names!!.isEmpty()) {
            runOnUiThread {
//                deviceName?.setText(names.first().sentenceCase())
            }

            return
        }

        val alertStatus = dataEvents?.getCurrentAlertStatus()
        when (alertStatus) {
            WearableConstants.AlertStatus.RUNNING,
            WearableConstants.AlertStatus.NOT_RUNNING -> {
                runOnUiThread {
                    updateCurrentAlertStatus(alertStatus)
                }
            }
        }
    }

    private fun getTintedDrawable(DrawableRes res: Int) : Drawable {
        val drawable = getDrawable(res)
        drawable.setColorFilter(
                getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY)
        return drawable
    }
}
