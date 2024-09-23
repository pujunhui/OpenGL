package com.pujh.camera.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

fun Bitmap.toSquare(): Bitmap {
    if (width == height) {
        return this
    }
    val size = max(width, height)
    val squareBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(squareBitmap)
    val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    val paint = Paint()
    paint.shader = shader
    val offsetX = (size - width) / 2
    val offsetY = (size - height) / 2
    canvas.translate(offsetX.toFloat(), offsetY.toFloat())
    canvas.drawRect(Rect(-offsetX, -offsetY, size - offsetX, size - offsetY), paint)
    return squareBitmap
}

fun Bitmap.save(path: String) {
    val file = File(path)
    FileOutputStream(file).use { out ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}

fun yuvToBitmap(
    yuv: ByteArray,
    format: Int,
    width: Int,
    height: Int,
    strides: IntArray? = null
): Bitmap {
    val yuvImage = YuvImage(yuv, format, width, height, strides)
    return ByteArrayOutputStream().use { stream ->
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, stream)
        BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
    }
}