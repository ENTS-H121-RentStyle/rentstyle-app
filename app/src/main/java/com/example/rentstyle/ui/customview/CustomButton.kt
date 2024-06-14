package com.example.rentstyle.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private var lightButton: Drawable
    private var darkButton: Drawable
    private var clickedButton: Drawable

    private var isClicked: Boolean = false

    init {
        lightButton = ContextCompat.getDrawable(context, R.drawable.shape_white_button) as Drawable
        darkButton = ContextCompat.getDrawable(context, R.drawable.shape_grey_button) as Drawable
        clickedButton = ContextCompat.getDrawable(context, R.drawable.shape_orange_button) as Drawable

        updateDrawable()
    }

    var isActivate: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isClicked) {
                    updateDrawable()
                } else {
                    background = clickedButton
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                }

                isClicked = !isClicked
                isActivate = isClicked
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onTouchEvent(event)
    }

    fun resetButton() {
        updateDrawable()
        isClicked = false
        isActivate = false
    }

    private fun updateDrawable() {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            background = darkButton
            setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            background = lightButton
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        updateDrawable()
    }
}