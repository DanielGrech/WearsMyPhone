package com.dgsd.android.wearsmyphone.activity

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import com.dgsd.android.wearsmyphone.R
import com.dgsd.android.wearsmyphone.view.MainActivityView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.wearable.Node
import com.dgsd.android.wearsmyphone.util.AppPreferences

public class MainActivity : ActionBarActivity(),
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var apiClient: GoogleApiClient? = null

    private var contentView: MainActivityView? = null

    private var prefs: AppPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ActionBarActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        prefs = AppPreferences.getInstance(this)

        contentView = findViewById(R.id.content_view) as MainActivityView

        apiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).build()
        apiClient!!.registerConnectionCallbacks(this)
        apiClient!!.registerConnectionFailedListener(this)
        apiClient!!.connect()

        contentView?.observeVibrateCheckChange()?.subscribe({prefs?.setVibrateEnabled(it)})
        contentView?.observeFlashlightChange()?.subscribe({prefs?.setFlashlightEnabled(it)})
    }

    override fun onDestroy() {
        if (apiClient!!.isConnected()) {
            apiClient!!.disconnect()
        }
        super<ActionBarActivity>.onDestroy()
    }

    override fun onConnected(bundle: Bundle?) {
        Thread {
            val nodes: List<Node>? = Wearable.NodeApi.getConnectedNodes(apiClient).await()?.getNodes()
            runOnUiThread {
                contentView!!.populateWithNodes(nodes)
            }
        }.start()
    }

    override fun onConnectionSuspended(reason: Int) {

    }

    override fun onConnectionFailed(result: ConnectionResult?) {
        // TODO: Show error!
    }
}
