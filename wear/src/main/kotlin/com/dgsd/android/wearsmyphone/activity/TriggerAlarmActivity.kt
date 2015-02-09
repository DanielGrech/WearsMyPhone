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
import com.dgsd.android.common.util.sentenceCase
import android.support.wearable.view.CircledImageView
import com.dgsd.android.wearsmyphone.util.onPreDraw
import com.dgsd.android.common.util.getCurrentAlertStatus

public class TriggerAlarmActivity : Activity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    private var apiClient: GoogleApiClient? = null

    private var deviceName: TextView? = null

    private var image: CircledImageView? = null

    private var nodeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.act_trigger_alarm)

        deviceName = findViewById(R.id.node_name) as TextView
        image = findViewById(R.id.action) as CircledImageView
        image?.onPreDraw {
            image?.setCircleRadius(image!!.getMeasuredHeight().toFloat().div(2))
        }
        image?.setOnClickListener {
            if (nodeId != null) {
                Wearable.MessageApi.sendMessage(apiClient, nodeId,
                        WearableConstants.Path.ALERT_START, null)
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
                    // TODO: No connected nodes!
                }
            } else {
                nodeId = nodes.first().getId()
                Wearable.MessageApi.sendMessage(apiClient, nodeId,
                        WearableConstants.Path.SEND_DEVICE_NAME, null).await()
            }
        }.start()
    }

    override fun onConnectionSuspended(reason: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult?) {
        showError()
    }

    private fun showError() {
        // TODO: Show error!
    }

    private fun updateCurrentAlertStatus(alertStatus: String) {
        when (alertStatus) {
            WearableConstants.AlertStatus.RUNNING -> {
                image?.setImageResource(R.drawable.ic_action_stop)
            }
            WearableConstants.AlertStatus.NOT_RUNNING -> {
                image?.setImageResource(R.drawable.ic_action_play)
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer?) {
        val names = dataEvents?.getDeviceNames(false)
        if (names != null && !names!!.isEmpty()) {
            runOnUiThread {
                deviceName?.setText(names.first().sentenceCase())
            }

            return
        }

        val alertStatus = dataEvents?.getCurrentAlertStatus()
        when (alertStatus) {
            WearableConstants.AlertStatus.RUNNING,
            WearableConstants.AlertStatus.NOT_RUNNING -> {
                updateCurrentAlertStatus(alertStatus)
            }
        }
    }
}
