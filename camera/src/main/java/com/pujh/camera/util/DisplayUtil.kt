package com.pujh.camera.util

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.Surface
import android.view.WindowManager

fun Context.getDisplayRotation(): Int {
    var display: Display? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display = this.display
    }
    if (display == null) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        display = windowManager.defaultDisplay
    }
    return when (display?.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> throw IllegalStateException("rotation is error")
    }
}
