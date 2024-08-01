package com.pujh.camera

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Surface
import android.view.WindowManager

fun Activity.getDisplayRotation(): Int {
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display!!
    } else {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay
    }
    return when (display.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> throw IllegalStateException("rotation is error")
    }
}