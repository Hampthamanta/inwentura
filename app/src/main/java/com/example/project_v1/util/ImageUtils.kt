package com.example.project_v1.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import androidx.camera.core.YuvToRgbConverter

object ImageUtils {
    private var converter: YuvToRgbConverter? = null

    private fun getConverter(context: Context): YuvToRgbConverter {
        return converter ?: YuvToRgbConverter(context).also { converter = it }
    }

    fun imageProxyToBitmap(context: Context, image: ImageProxy): Bitmap {
        val conv = getConverter(context)
        val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val img = image.image
        if (img != null) {
            conv.yuvToRgb(img, bitmap)
        }
        return bitmap
    }

    fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
