package com.example.rentstyle.ui.customview

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

class CustomDividerBold @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    private var lightDivider: Drawable
    private var darkDivider: Drawable

    init {
        lightDivider = ContextCompat.getDrawable(context, R.drawable.shape_line_bold_light) as Drawable
        darkDivider = ContextCompat.getDrawable(context, R.drawable.shape_line_bold_dark) as Drawable

        updateDrawable()
    }

    private fun updateDrawable() {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            setImageDrawable(darkDivider)
        } else {
            setImageDrawable(lightDivider)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        updateDrawable()
    }
}