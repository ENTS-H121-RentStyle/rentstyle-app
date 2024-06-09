package com.example.rentstyle.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

class CustomRatingStarImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {

    private var customAttribute: Int = 0
    private var ratingStarImage: Drawable? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomRatingStarImage,
            0, 0).apply {
            try {
                customAttribute = getInt(R.styleable.CustomRatingStarImage_ratingScore, 0)
                updateRatingStarImage(customAttribute)
            } finally {
                recycle()
            }
        }
    }

    var ratingScore: Int
        get() = customAttribute
        set(value) {
            customAttribute = value
            updateRatingStarImage(value)
        }

    private fun updateRatingStarImage(ratingScore: Int) {
        ratingStarImage = when (ratingScore) {
            1 -> ContextCompat.getDrawable(context, R.drawable.ic_one_star)
            2 -> ContextCompat.getDrawable(context, R.drawable.ic_two_star)
            3 -> ContextCompat.getDrawable(context, R.drawable.ic_three_star)
            4 -> ContextCompat.getDrawable(context, R.drawable.ic_four_star)
            5 -> ContextCompat.getDrawable(context, R.drawable.ic_five_star)
            else -> null
        }
        setImageDrawable(ratingStarImage)
    }
}