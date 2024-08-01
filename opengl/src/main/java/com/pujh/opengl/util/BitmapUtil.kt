package com.pujh.opengl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

fun Context.loadBitmap(@DrawableRes bitmapId: Int): Bitmap {
    val options = BitmapFactory.Options()
    options.inScaled = false
    return BitmapFactory.decodeResource(resources, bitmapId, options)
}

fun Context.loadAssetsBitmap(name: String): Bitmap {
    assets.open(name).use { input ->
        return BitmapFactory.decodeStream(input)
    }
}