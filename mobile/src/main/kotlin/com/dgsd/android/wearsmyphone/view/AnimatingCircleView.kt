package com.dgsd.android.wearsmyphone.view

import android.content.Context
import android.util.AttributeSet
import android.graphics.Paint
import com.dgsd.android.wearsmyphone.R
import android.graphics.Canvas
import android.view.animation.Interpolator
import android.view.animation.AccelerateInterpolator
import rx.subjects.PublishSubject
import rx.android.schedulers.AndroidSchedulers
import rx.Observable
import android.graphics.RectF

public class AnimatingCircleView(context: Context, attrs: AttributeSet) :
        BaseInterpolatedView(context, attrs) {

    class object {
        private val INTERPOLATOR = AccelerateInterpolator(1.5f)
    }

    private val paint: Paint

    private val rect: RectF

    private val onAnimationEndSubject: PublishSubject<AnimatingCircleView>

    {
        paint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG))
        paint.setColor(context.getResources().getColor(R.color.primary))
        paint.setStyle(Paint.Style.FILL_AND_STROKE)

        rect = RectF()

        onAnimationEndSubject = PublishSubject.create()
        onAnimationEndSubject.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect.left = 0f
        rect.right = canvas.getWidth().toFloat()
        rect.top = 0f
        rect.bottom = canvas.getHeight().toFloat()
        val percentage = if (isInEditMode()) 1f else getInterpolatedValue()

        canvas.drawArc(rect, 270f, 360f.times(percentage), true, paint)
    }

    override fun getInterpolator(): Interpolator {
        return AnimatingCircleView.INTERPOLATOR
    }

    override fun onAnimatorEnd() {
        super.onAnimatorEnd()
        onAnimationEndSubject.onNext(this)
    }

    public fun observeAnimationEnd() : Observable<AnimatingCircleView> {
        return onAnimationEndSubject.asObservable()
    }

    public fun reveal() {
        animateTo(1f)
    }
}
