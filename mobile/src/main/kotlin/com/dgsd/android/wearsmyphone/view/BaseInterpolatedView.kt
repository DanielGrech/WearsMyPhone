package com.dgsd.android.wearsmyphone.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.animation.AnimatorListenerAdapter

/**
 * Base class for custom views which animate based on an interpolated value
 */
public abstract class BaseInterpolatedView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    class object {
        private val DEFAULT_ANIMATION_DURATION = 1000L;

        private val DEFAULT_START_DELAY = 100L;

        private val DEFAULT_INTERPOLATOR = DecelerateInterpolator(1.5f);
    }

    private var interpolatedValue: Float = 0f;

    public fun getInterpolatedValue() : Float {
        return interpolatedValue;
    }

    public fun setInterpolatedValue(interpolatedValue: Float) {
        this.interpolatedValue = interpolatedValue;
        invalidate();
    }

    public open fun getAnimationDuration() : Long {
        return DEFAULT_ANIMATION_DURATION;
    }

    public open fun getStartDelay() : Long {
        return DEFAULT_START_DELAY;
    }

    /**
     * @return The interpolator to use for the animation
     */
    protected open fun getInterpolator() : Interpolator {
        return DEFAULT_INTERPOLATOR;
    }

    protected open fun onAnimatorEnd() {
        // No-op .. for subclasses to override
    }

    protected fun animateTo(target: Float) {
        val anim = ObjectAnimator.ofFloat(this, "interpolatedValue", target);
        anim.setInterpolator(getInterpolator());
        anim.setDuration(getAnimationDuration());
        anim.setStartDelay(getStartDelay());
        anim.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimatorEnd()
            }
        })
        anim.start();
    }
}