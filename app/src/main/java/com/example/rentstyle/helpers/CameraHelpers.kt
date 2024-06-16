package com.example.rentstyle.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraHelpers {
    fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun bitmapToUri(bitmap: Bitmap, context: Context): Uri {
        val fileNameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

        val timeStamp = SimpleDateFormat(fileNameFormat, Locale.getDefault()).format(
            Date()
        )

        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "$timeStamp.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}