package com.dgsd.android.wearsmyphone.view

import android.content.Context
import android.widget.ScrollView
import android.util.AttributeSet
import android.widget.TextView
import com.dgsd.android.wearsmyphone.R
import android.content.res.Resources
import android.text.TextUtils
import rx.Observable
import rx.android.view.OnClickEvent
import rx.android.view.ViewObservable
import com.dgsd.android.wearsmyphone.model.DurationOption

fun String.sentenceCase(): String {
    when {
        this.length() <= 1 -> return this
        else -> return "${Character.toUpperCase(this[0])}${this.substring(1)}"
    }
}

public class MainActivityView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {

    private var pageTitle: TextView? = null
    private var pageSubtitle: TextView? = null

    private var ringtoneSetting: SettingItemView? = null
    private var durationSetting: SettingItemView? = null
    private var vibrationSetting: SettingItemView? = null
    private var flashLightSetting: SettingItemView? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        pageTitle = findViewById(R.id.page_title) as TextView
        pageSubtitle = findViewById(R.id.page_subtitle) as TextView

        ringtoneSetting = findViewById(R.id.ringtone) as SettingItemView
        durationSetting = findViewById(R.id.duration) as SettingItemView
        vibrationSetting = findViewById(R.id.vibrate) as SettingItemView
        flashLightSetting = findViewById(R.id.flashlight) as SettingItemView

        val clickListener = { (it: SettingItemView) -> it.toggle() }

        vibrationSetting?.setOnClickListener { clickListener(it as SettingItemView) }
        flashLightSetting?.setOnClickListener { clickListener(it as SettingItemView) }
    }

    public fun setTitle(title: String) {
        pageTitle?.setText(title);
    }

    public fun setSubtitle(subtitle: String) {
        pageSubtitle?.setText(subtitle);
    }

    public fun populateWithPeers(peerNames: Set<String>?) {
        val res: Resources = getResources()
        val title: String
        val subtitle: String

        when {
            peerNames == null || peerNames.isEmpty() -> {
                title = res.getString(R.string.title_no_devices)
                subtitle = res.getString(R.string.subtitle_no_devices)
            }
            peerNames.size() == 1 -> {
                title = peerNames.first().sentenceCase()
                subtitle = res.getString(R.string.connected)
            }
            else -> {
                title = res.getString(R.string.x_devices_connected)
                subtitle = TextUtils.join(", ", peerNames.map { n -> n.sentenceCase() })
            }
        }

        setTitle(title);
        setSubtitle(subtitle);
    }

    public fun setDurationOption(option: DurationOption) {
        val text: String
        when (option) {
            DurationOption.INFINITE -> {
                text = getResources().getString(R.string.alert_for_infinite)
            }
            else -> {
                val optionText = getResources().getString(option.displayStringRes)
                text = getResources().getString(R.string.alert_for_x, optionText)
            }
        }
        durationSetting?.setSecondary(text)
    }

    public fun setVibrateStatus(enabled: Boolean) {
        vibrationSetting?.setChecked(enabled)
    }

    public fun observeVibrateCheckChange() : Observable<Boolean> {
        return vibrationSetting?.observeCheckChange() ?: Observable.empty()
    }

    public fun setFlashlightStatus(enabled: Boolean) {
        flashLightSetting?.setChecked(enabled)
    }

    public fun observeFlashlightChange() : Observable<Boolean> {
        return flashLightSetting?.observeCheckChange() ?: Observable.empty()
    }

    public fun observeDurationClick(): Observable<OnClickEvent> {
        return ViewObservable.clicks(durationSetting)
    }

    public fun observeRingtoneClick(): Observable<OnClickEvent> {
        return ViewObservable.clicks(ringtoneSetting)
    }
}