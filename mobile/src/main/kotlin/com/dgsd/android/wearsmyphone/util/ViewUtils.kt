package com.dgsd.android.wearsmyphone.util

import android.view.View
import android.view.ViewTreeObserver
import android.os.Build
import android.animation.AnimatorListenerAdapter
import android.animation.Animator

public fun View.onPreDraw(action: () -> Unit) {
    getViewTreeObserver().addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            getViewTreeObserver().removeOnPreDrawListener(this)

            val shouldExecute: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
    if (isVisible()) {
        setAlpha(1f)
    } else {
        setAlpha(0f)
        show()

        animate().cancel()
        animate().alpha(1f).setListener(null)
    }
}

public fun View.fadeOut() {
    animate().cancel()
    animate().alpha(0f).setListener(object: AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            hide()
        }
    });
}


