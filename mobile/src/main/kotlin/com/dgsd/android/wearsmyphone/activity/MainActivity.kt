package com.dgsd.android.wearsmyphone.activity

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import com.dgsd.android.wearsmyphone.R
import com.dgsd.android.wearsmyphone.view.MainActivityView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.common.ConnectionResult
import com.dgsd.android.wearsmyphone.util.AppPreferences
import com.google.android.gms.wearable.DataApi
import com.google.android.gms.wearable.DataEventBuffer
import java.util.HashSet
import com.google.android.gms.wearable.DataEvent
import com.dgsd.android.common.WearableConstants
import com.google.android.gms.wearable.DataMapItem
import android.text.TextUtils
import com.dgsd.android.wearsmyphone.fragment.DurationChoiceDialogFragment

public class MainActivity : ActionBarActivity(), DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private var apiClient: GoogleApiClient? = null

    private var contentView: MainActivityView? = null

    private var prefs: AppPreferences? = null

    private val peerNames: MutableSet<String> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ActionBarActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        prefs = AppPreferences.getInstance(this)

        contentView = findViewById(R.id.content_view) as MainActivityView

        apiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        apiClient!!.connect()
    }

    override fun onResume() {
        super<ActionBarActivity>.onResume()

        contentView?.setVibrateStatus(prefs!!.isVibrateEnabled())
        contentView?.setFlashlightStatus(prefs!!.isFlashlightEnabled())

        contentView?.observeVibrateCheckChange()?.subscribe({ prefs?.setVibrateEnabled(it) })
        contentView?.observeFlashlightChange()?.subscribe({ prefs?.setFlashlightEnabled(it) })
        contentView?.observeDurationClick()?.subscribe({ showDurationDialog() })
        contentView?.observeDurationClick()?.subscribe({ showRingtoneDialog() })
    }

    override fun onDestroy() {
        if (apiClient!!.isConnected()) {
            Wearable.DataApi.removeListener(apiClient, this)
            apiClient!!.disconnect()
        }
        peerNames.clear()
        super<ActionBarActivity>.onDestroy()
    }

    override fun onConnected(bundle: Bundle?) {
        Wearable.DataApi.addListener(apiClient, this)

        Thread {
            val nodes = Wearable.NodeApi.getConnectedNodes(apiClient).await()?.getNodes()
            if (nodes == null || nodes.isEmpty()) {
                runOnUiThread {
                    contentView?.populateWithPeers(null)
                }
            } else {
                nodes.forEach { node ->
                    Wearable.MessageApi.sendMessage(apiClient, node.getId(),
                            WearableConstants.Path.SEND_DEVICE_NAME, null).await()
                }
            }
        }.start()
    }

    override fun onConnectionSuspended(reason: Int) {

    }

    override fun onConnectionFailed(result: ConnectionResult?) {
        contentView?.populateWithPeers(null)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer?) {
        var changed = false
        dataEvents?.forEach { event ->
            if (DataEvent.TYPE_CHANGED.equals(event.getType())) {
                val dataItem = event.getDataItem()
                if (WearableConstants.Path.DEVICE_NAME.equals(dataItem.getUri().getPath())) {
                    val deviceName = DataMapItem.fromDataItem(dataItem)
                            .getDataMap()?.getString(WearableConstants.Data.DEVICE_NAME)
                    if (!TextUtils.isEmpty(deviceName)) {
                        peerNames.add(deviceName!!)
                        changed = true
                    }
                }
            }
        }

        if (changed) {
            runOnUiThread {
                contentView!!.populateWithPeers(peerNames)
            }
        }
    }

    fun showDurationDialog() {
        DurationChoiceDialogFragment.newInstance(this)
                .show(getSupportFragmentManager(), "duration_dialog")
    }

    fun showRingtoneDialog() {
    }
}
