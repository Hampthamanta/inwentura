package com.example.project_v1.utils

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.camera.core.YuvToRgbConverter

fun ImageProxy.toBitmap(converter: YuvToRgbConverter): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    converter.yuvToRgb(image!!, bitmap)
    return bitmap
}
