package com.dgsd.android.wearsmyphone.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dgsd.android.wearsmyphone.model.DurationOption
import android.net.Uri
import android.media.RingtoneManager

public class AppPreferences private(context: Context) {

    private val preferences: SharedPreferences

    class object {

        private val PREF_KEY_VIBRATE_ENABLED = "_vibrate_enabled"
        private val PREF_KEY_FLASH_LIGHT_ENABLED = "_flashlight_enabled"
        private val PREF_KEY_ALERT_DURATION = "_alert_duration"
        private val PREF_KEY_RINGTONE_URI = "_ringtone_uri"

        private var instance: AppPreferences? = null

        public fun getInstance(context: Context): AppPreferences {
            if (instance == null) {
                instance = AppPreferences(context.getApplicationContext())
            }
            return instance!!
        }
    }

    {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    public fun setRingtoneUri(uri: Uri) {
        val uriToSave = uri.toString() ?:
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString()
        setString(PREF_KEY_RINGTONE_URI, uriToSave)
    }

    public fun getRingtoneUri() : Uri {
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val uriFromPrefs = preferences.getString(PREF_KEY_RINGTONE_URI, null)

        return if (uriFromPrefs == null) defaultUri else Uri.parse(uriFromPrefs)
    }

    public fun setDurationForAlert(duration: Long) {
        setLong(PREF_KEY_ALERT_DURATION, duration)
    }

    public fun getDurationForAlert(): Long {
        return preferences.getLong(PREF_KEY_ALERT_DURATION,
                DurationOption.INFINITE.durationInSeconds())
    }

    public fun setFlashlightEnabled(enabled: Boolean) {
        setBoolean(PREF_KEY_FLASH_LIGHT_ENABLED, enabled)
    }

    public fun isFlashlightEnabled() : Boolean {
        return preferences.getBoolean(PREF_KEY_FLASH_LIGHT_ENABLED, false)
    }

    public fun setVibrateEnabled(enabled: Boolean) {
        setBoolean(PREF_KEY_VIBRATE_ENABLED, enabled)
    }

    public fun isVibrateEnabled() : Boolean {
        return preferences.getBoolean(PREF_KEY_VIBRATE_ENABLED, true)
    }

    private fun setString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    private fun setBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    private fun setLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    private fun setInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
}
