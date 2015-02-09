package com.dgsd.android.wearsmyphone.activity

import android.os.Bundle
import android.support.v7.app.ActionBarActivity

import com.dgsd.android.wearsmyphone.util.AppPreferences
import com.dgsd.android.wearsmyphone.R
import android.os.Vibrator
import android.content.Context
import android.view.WindowManager
import android.view.KeyEvent
import timber.log.Timber
import java.util.concurrent.TimeUnit
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.AudioManager
import rx.android.content.ContentObservable
import android.content.IntentFilter
import rx.Subscription
import android.os.Build
import android.media.AudioAttributes
import com.dgsd.android.wearsmyphone.view.AlertActivityView
import com.dgsd.android.wearsmyphone.service.NoisyNotificationService
import android.hardware.Camera
import android.content.pm.PackageManager
import android.hardware.Camera.Parameters
import android.view.SurfaceView
import android.view.SurfaceHolder

public class AlertActivity : ActionBarActivity() {

    private var prefs: AppPreferences? = null

    private var camera: Camera? = null

    private var vibrator: Vibrator? = null

    private var audioManager : AudioManager? = null

    private var ringtone : Ringtone? = null

    private var originalVolumeLevel : Int? = null

    private var stopObservable : Subscription? = null

    private val finishRunnable = Runnable { finish() }

    private var view : AlertActivityView? = null

    class object {
        public val ACTION_STOP_NOTIFY: String = "_action_stop_notify"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<ActionBarActivity>.onCreate(savedInstanceState)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                .or(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                .or(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                .or(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON));

        setContentView(R.layout.act_alert)

        prefs = AppPreferences.getInstance(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        ringtone = RingtoneManager.getRingtone(this, prefs?.getRingtoneUri())

        view = findViewById(R.id.content) as AlertActivityView
        view?.observeCircleClick()?.subscribe({ finish() })

        stopObservable = ContentObservable.fromBroadcast(this, IntentFilter(ACTION_STOP_NOTIFY))
                .subscribe({ intent -> finish() })

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        view?.runEnterAnimation()
    }

    override fun onDestroy() {
        stopObservable?.unsubscribe()
        super.onDestroy()
    }

    override fun finish() {
        NoisyNotificationService.notifyAlertChange(this, false)
        super.finish()
    }

    override fun onResume() {
        super.onResume()

        val stream = AudioManager.STREAM_ALARM
        audioManager?.requestAudioFocus(null, stream, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        originalVolumeLevel = audioManager?.getStreamVolume(stream)

        Timber.d("MAX: %s", audioManager!!.getStreamMaxVolume(stream) )

        audioManager?.setStreamVolume(stream, audioManager!!.getStreamMaxVolume(stream),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)

        if (isVibratorEnabled()) {
            vibrator?.vibrate(longArray(0L, 500L, 500L), 0)
        }

        if (isFlashEnabled()) {
            startFlash()
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            ringtone?.setStreamType(stream)
        } else {
            ringtone?.setAudioAttributes(AudioAttributes.Builder()
                    .setLegacyStreamType(stream)
                    .build())
        }
        ringtone?.play()

        NoisyNotificationService.notifyAlertChange(this, true)

        val alertDuration = TimeUnit.SECONDS.toMillis(prefs!!.getDurationForAlert())

        Timber.d("Alert duration = %s millis", alertDuration)

        view?.postDelayed(finishRunnable, alertDuration)
    }

    override fun onPause() {
        view?.removeCallbacks(finishRunnable)

        audioManager?.abandonAudioFocus(null)
        if (originalVolumeLevel != null) {
            audioManager?.setStreamVolume(AudioManager.STREAM_ALARM, originalVolumeLevel!!,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
        }

        if (isFlashEnabled()) {
            stopFlash()
        }

        if (isVibratorEnabled()) {
            vibrator?.cancel()
        }

        ringtone?.stop()

        super.onPause()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (event.getKeyCode()) {
            KeyEvent.KEYCODE_POWER -> {
                finish();
                return true
            }

            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_MUTE,
            KeyEvent.KEYCODE_CAMERA,
            KeyEvent.KEYCODE_FOCUS ->  {
                // TODO: Shut up!
                return true
            }

            else -> return super.dispatchKeyEvent(event)
        }
    }

    private fun isVibratorEnabled(): Boolean {
        return vibrator?.hasVibrator()?.and(prefs?.isVibrateEnabled() ?: false) ?: false
    }

    private fun isFlashEnabled(): Boolean {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) &&
                (prefs?.isFlashlightEnabled() ?: true)
    }

    private fun startFlash() {
        val preview = findViewById(R.id.preview) as SurfaceView;
        val holder = preview.getHolder();
        holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                try {
                    camera?.setPreviewDisplay(holder)
                } catch (throwable: Throwable) {
                    // Oh well..
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                try {
                    camera?.stopPreview()
                } catch (ex: Exception) {
                    // Oh well..
                }
            }
        })

        camera = Camera.open()

        try {
            camera?.setPreviewDisplay(holder)
            val params = camera?.getParameters()
            params?.setFlashMode(Parameters.FLASH_MODE_TORCH)
            params?.setFocusMode(Parameters.FOCUS_MODE_INFINITY)
            camera?.setParameters(params)
            camera?.startPreview()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error starting flashlight")
        }
    }

    private fun stopFlash() {
        try {
            val params = camera?.getParameters()
            params?.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera?.setParameters(params);
            camera?.stopPreview();
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error stopping flashlight")
        } finally {
            camera?.release()
        }
    }
}
