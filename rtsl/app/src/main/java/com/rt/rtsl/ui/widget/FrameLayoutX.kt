package com.rt.rtsl.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

class FrameLayoutX @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val touchSlop= ViewConfiguration.get(context).scaledTouchSlop*1.5F
    var allowLeftRightTouch = true
    var downX = 0f
    var downY = 0f
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    {
        if (ev.action == MotionEvent.ACTION_DOWN)
        {
            downX = ev.x
            downY = ev.y
        }
        else if (ev.action == MotionEvent.ACTION_MOVE) {
            val moveX: Float = ev.x
            val moveY: Float = ev.y
            if (!allowLeftRightTouch && abs(moveX - downX) > touchSlop) {
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_MOVE) {
            val moveX: Float = ev.x
            val moveY: Float = ev.y
            if (!allowLeftRightTouch && abs(moveX - downX) > touchSlop) {
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}