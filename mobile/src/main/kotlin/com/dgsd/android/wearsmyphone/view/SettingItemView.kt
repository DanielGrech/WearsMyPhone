package com.dgsd.android.wearsmyphone.view

import android.util.AttributeSet
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import com.dgsd.android.wearsmyphone.R
import android.view.LayoutInflater
import android.widget.Switch
import android.view.View
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.PublishSubject
import rx.Observable
import android.widget.Checkable

public class SettingItemView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), Checkable {

    private val primary: TextView
    private val secondary: TextView
    private val icon: ImageView
    private val toggle: Switch

    private val onCheckChangedSubject: PublishSubject<Boolean>

    {
        setOrientation(LinearLayout.HORIZONTAL)
        LayoutInflater.from(context).inflate(R.layout.li_setting_item, this, true)

        onCheckChangedSubject = PublishSubject.create();
        onCheckChangedSubject.subscribeOn(AndroidSchedulers.mainThread())

        primary = findViewById(R.id.primary) as TextView
        secondary = findViewById(R.id.secondary) as TextView
        icon = findViewById(R.id.icon) as ImageView
        toggle = findViewById(R.id.toggle) as Switch

        toggle.setOnCheckedChangeListener { (btn, isChecked) ->
            onCheckChangedSubject.onNext(isChecked)
        }

        val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView, 0, 0)
        try {
            primary.setText(ta.getString(R.styleable.SettingItemView_primary))
            secondary.setText(ta.getString(R.styleable.SettingItemView_secondary))
            icon.setImageDrawable(ta.getDrawable(R.styleable.SettingItemView_settingIcon))

            if (ta.getBoolean(R.styleable.SettingItemView_showSwitch, false)) {
                toggle.setVisibility(View.VISIBLE)
            } else {
                toggle.setVisibility(View.GONE)
            }
        } finally {
            ta.recycle()
        }
    }

    public fun setSecondary(text: String) {
        secondary.setText(text)
    }

    public fun observeCheckChange() : Observable<Boolean> {
        return onCheckChangedSubject.asObservable()
    }

    override fun setChecked(checked: Boolean) {
        toggle.setChecked(checked)
    }

    override fun isChecked(): Boolean {
        return toggle.isChecked()
    }

    override fun toggle() {
        toggle.toggle()
    }

}