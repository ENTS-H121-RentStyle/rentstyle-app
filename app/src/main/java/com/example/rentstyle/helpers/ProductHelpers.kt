package com.example.rentstyle.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

object ProductHelpers {
    fun getProductCategory (context: Context): List<String> {
        val firstOption = ContextCompat.getString(context, R.string.txt_choose)
        val secondOption = ContextCompat.getString(context, R.string.txt_traditional_sets)
        val thirdOption = ContextCompat.getString(context, R.string.txt_party_sets)
        val forthOption = ContextCompat.getString(context, R.string.txt_formal_sets)
        val fifthOption = ContextCompat.getString(context, R.string.txt_character_cosplay_sets)

        return arrayListOf(firstOption, secondOption, thirdOption, forthOption, fifthOption)
    }

    fun getCategoryValue(category: String): String {
        return if (category == "Traditional sets" || category == "Setelan adat") {
            "Adat"
        } else if (category == "Party sets" || category == "Setelan pesta") {
            "Pesta"
        } else if (category == "Formal sets" || category == "Setelan formal") {
            "Formal"
        } else if (category == "Character cosplay sets" || category == "Setelan cosplay karakter") {
            "Cosplay"
        } else {
            "Default"
        }
    }

    fun getProductSize (context: Context): List<String> {
        return arrayListOf("Choose", "S", "M", "L", "XL")
    }
}