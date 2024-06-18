package com.example.rentstyle.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHelpers {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getReturnDate(dateString: String, days: Long): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dateString, dateFormatter)
        val updatedDate = date.plusDays(days)
        return updatedDate.format(dateFormatter)
    }
}