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
        return when (category) {
            "Traditional sets", "Setelan adat" -> {
                "Adat"
            }
            "Party sets", "Setelan pesta" -> {
                "Pesta"
            }
            "Formal sets", "Setelan formal" -> {
                "Formal"
            }
            "Character cosplay sets", "Setelan cosplay karakter" -> {
                "Cosplay"
            }
            else -> {
                ""
            }
        }
    }

    fun getProductSize (): List<String> {
        return arrayListOf("Choose", "S", "M", "L", "XL")
    }
}