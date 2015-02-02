package com.dgsd.android.wearsmyphone.view

import android.content.Context
import android.widget.ScrollView
import android.util.AttributeSet
import android.widget.TextView
import com.dgsd.android.wearsmyphone.R
import com.google.android.gms.wearable.Node
import android.content.res.Resources
import android.text.TextUtils
import rx.Observable

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
    }

    public fun setTitle(title: String) {
        pageTitle!!.setText(title);
    }

    public fun setSubtitle(subtitle: String) {
        pageSubtitle!!.setText(subtitle);
    }

    public fun populateWithNodes(nodes: List<Node>?) {
        val res: Resources = getResources()
        val title: String
        val subtitle: String

        when {
            nodes == null || nodes.isEmpty() -> {
                title = res.getString(R.string.title_no_devices)
                subtitle = res.getString(R.string.subtitle_no_devices)
            }
            nodes.size() == 1 -> {
                title = nodes.first().getDisplayName()
                subtitle = res.getString(R.string.connected)
            }
            else -> {
                title = res.getString(R.string.x_devices_connected)
                subtitle = TextUtils.join(", ", nodes.map { n -> n.getDisplayName() })
            }
        }

        setTitle(title);
        setSubtitle(subtitle);
    }

    public fun observeVibrateCheckChange() : Observable<Boolean> {
        return vibrationSetting?.observeCheckChange() ?: Observable.empty()
    }

    public fun observeFlashlightChange() : Observable<Boolean> {
        return flashLightSetting?.observeCheckChange() ?: Observable.empty()
    }
}