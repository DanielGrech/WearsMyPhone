package com.dgsd.android.wearsmyphone.util

import android.view.View
import android.view.ViewTreeObserver
import android.os.Build
import android.animation.AnimatorListenerAdapter
import android.animation.Animator
import android.view.animation.DecelerateInterpolator

private val SCALE_TARGET = 0.8f

private val defaultInterpolator = DecelerateInterpolator(1.5f)

public fun View.onPreDraw(action: () -> Unit) {
    getViewTreeObserver().addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            getViewTreeObserver().removeOnPreDrawListener(this)

            val shouldExecute: Boolean
            if (Build.VERSION.SDK_INT >= 19) {
                shouldExecute = isAttachedToWindow();
            } else {
                shouldExecute = isShown();
            }

            if (shouldExecute) {
                action()
            }

            return true
        }
    })
}

public fun View.hide() {
    setVisibility(View.GONE)
}

public fun View.show() {
    setVisibility(View.VISIBLE)
}

public fun View.isGone() : Boolean {
    return this.getVisibility() == View.GONE
}

public fun View.isVisible() : Boolean {
    return this.getVisibility() == View.VISIBLE
}

public fun View.fadeIn() {
    fadeIn(false)
}

public fun View.fadeAndScaleIn() {
    fadeIn(true)
}

private fun View.fadeIn(scale: Boolean) {
    if (isVisible()) {
        setAlpha(1f)
        setScaleX(1f)
        setScaleY(1f)
    } else {
        setAlpha(0f)

        if (scale) {
            setScaleX(SCALE_TARGET)
            setScaleY(SCALE_TARGET)
        }

        show()

        animate().cancel()

        var anim = animate().alpha(1f)
        if (scale) {
            anim = anim.scaleX(1f).scaleY(1f)
        }
        anim.setInterpolator(defaultInterpolator).setListener(null)
    }
}

public fun View.fadeOut() {
    fadeOut(false)
}

public fun View.fadeAndScaleOut() {
    fadeOut(true)
}

private fun View.fadeOut(scale: Boolean) {
    animate().cancel()
    var anim = animate().alpha(0f)
    if (scale) {
        anim = anim.scaleX(SCALE_TARGET).scaleY(SCALE_TARGET)
    }

    anim.setInterpolator(defaultInterpolator).setListener(object: AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            hide()
        }
    });
}


