package com.example.rentstyle.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

object UserHelpers {
    fun getUserGender(context: Context): List<String> {
        val firstOption = ContextCompat.getString(context, R.string.txt_choose)
        val secondOption = ContextCompat.getString(context, R.string.txt_male)
        val thirdOption = ContextCompat.getString(context, R.string.txt_female)

        return arrayListOf(firstOption, secondOption, thirdOption)
    }

    fun getUserGenderValue(gender: String): String {
        return when (gender) {
            "Male", "Pria" -> {
                "Pria"
            }
            "Female", "Wanita" -> {
                "Wanita"
            }
            else -> {
                ""
            }
        }
    }
}