package com.example.rentstyle.helpers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CacheImageManager {
    private const val TEMP_IMAGE_FILE_PREFIX = "rs_product_"
    fun saveTempImage(context: Context): Uri {
        val imageSuffix = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH)
            .format( Date() )
        return File(context.cacheDir, "$TEMP_IMAGE_FILE_PREFIX$imageSuffix.jpg").toUri()
    }

    fun clearTempImages(context: Context) {
        val cacheDir = context.cacheDir
        val tempFiles = cacheDir.listFiles { file ->
            file.name.startsWith(TEMP_IMAGE_FILE_PREFIX)
        }
        tempFiles?.forEach { file ->
            file.delete()
        }
    }
}