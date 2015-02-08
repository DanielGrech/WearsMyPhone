package com.dgsd.android.wearsmyphone.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.dgsd.android.wearsmyphone.R
import rx.Observable
import rx.android.view.OnClickEvent
import rx.android.view.ViewObservable
import com.dgsd.android.wearsmyphone.util.fadeAndScaleIn
import com.dgsd.android.wearsmyphone.util.onPreDraw

public class AlertActivityView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var circleBackground : AnimatingCircleView? = null

    private var label : TextView? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        label = findViewById(R.id.label) as TextView
        circleBackground = findViewById(R.id.circle_background) as AnimatingCircleView
    }

    public fun runEnterAnimation() {
        circleBackground?.observeAnimationEnd()?.subscribe({ view ->
            label?.fadeAndScaleIn()
        })
        circleBackground?.onPreDraw {
            circleBackground?.reveal()
        }
    }

    public fun observeCircleClick() : Observable<OnClickEvent> {
        return ViewObservable.clicks(circleBackground)
    }
}