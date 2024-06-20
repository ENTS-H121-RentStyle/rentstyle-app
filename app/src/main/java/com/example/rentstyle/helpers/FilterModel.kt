package com.example.rentstyle.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.rentstyle.R

object FilterModel {

    fun getNotificationFilter(context: Context) : ArrayList<String> {
        return arrayListOf(
            ContextCompat.getString(context, R.string.txt_new_notification),
            ContextCompat.getString(context, R.string.txt_status_notification),
            ContextCompat.getString(context, R.string.txt_system_notification))
    }
}