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
import com.dgsd.android.common.WearableConstants
import com.dgsd.android.wearsmyphone.fragment.DurationChoiceDialogFragment
import com.dgsd.android.wearsmyphone.model.DurationOption
import rx.functions.Action1
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.content.Context
import android.os.Vibrator
import android.media.RingtoneManager
import android.net.Uri
import com.dgsd.android.wearsmyphone.service.NoisyNotificationService
import com.dgsd.android.common.util.getDeviceNames

public class MainActivity : ActionBarActivity(), DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    class object {
        private val REQUEST_CODE_RINGTONE = 0x01
    }

    private var apiClient: GoogleApiClient? = null

    private var contentView: MainActivityView? = null

    private var prefs: AppPreferences? = null

    private val peerNames: MutableSet<String> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ActionBarActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        getSupportActionBar().setElevation(0f);

        prefs = AppPreferences.getInstance(this)

        contentView = findViewById(R.id.content_view) as MainActivityView

        apiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        apiClient!!.connect()

        contentView?.setVibrateStatus(prefs!!.isVibrateEnabled())
        contentView?.setFlashlightStatus(prefs!!.isFlashlightEnabled())

        contentView?.observeVibrateCheckChange()?.subscribe({ prefs?.setVibrateEnabled(it) })
        contentView?.observeFlashlightChange()?.subscribe({ prefs?.setFlashlightEnabled(it) })
        contentView?.observeDurationClick()?.subscribe({ showDurationDialog() })
        contentView?.observeRingtoneClick()?.subscribe({ showRingtoneDialog() })

        val durationOption = DurationOption.fromDurationInSeconds(prefs!!.getDurationForAlert())
                ?: DurationOption.INFINITE
        contentView?.setDurationOption(durationOption)

        setRingtoneTitle()

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        contentView?.setVibrateSupported(vibrator.hasVibrator())
    }

    override fun onDestroy() {
        if (apiClient!!.isConnected()) {
            Wearable.DataApi.removeListener(apiClient, this)
            apiClient!!.disconnect()
        }
        peerNames.clear()
        super<ActionBarActivity>.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.act_main, menu)
        return super<ActionBarActivity>.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.test_alert -> {
                startAlert()
                return true
            }
            else -> return super<ActionBarActivity>.onOptionsItemSelected(item)
        }
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
        val names = dataEvents?.getDeviceNames(true)
        if (names != null && !names.isEmpty()) {
            runOnUiThread {
                peerNames.addAll(names)
                contentView!!.populateWithPeers(peerNames)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode.equals(REQUEST_CODE_RINGTONE)) {
            val uri = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            prefs?.setRingtoneUri(uri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            setRingtoneTitle()
        } else {
            super<ActionBarActivity>.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startAlert() {
        NoisyNotificationService.startNotify(this)
    }

    private fun showDurationDialog() {
        val tag = "duration_dialog"
        val fm = getSupportFragmentManager()

        val existingDialog = fm.findFragmentByTag(tag)
        if (existingDialog != null && existingDialog is DurationChoiceDialogFragment) {
            existingDialog.dismissAllowingStateLoss()
        }

        val dialog =  DurationChoiceDialogFragment.newInstance(this)
        dialog.setOnItemSelectionAction(Action1<DurationOption> { option ->
            contentView?.setDurationOption(option)
        })

        dialog.show(fm, tag)
    }

    private fun showRingtoneDialog() {
        val ringtoneIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, prefs?.getRingtoneUri())
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.setting_title_ringtone))

        startActivityForResult(ringtoneIntent, REQUEST_CODE_RINGTONE)
    }

    private fun setRingtoneTitle() {
        val title = RingtoneManager.getRingtone(this, prefs?.getRingtoneUri()).getTitle(this)
        contentView?.setRingtoneTitle(title)
    }
}
