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

fun calcDisplayRotation(
    isFrontCamera: Boolean,
    cameraOrientation: Int,
    screenRotation: Int
): Int {
    var result: Int
    if (isFrontCamera) {
        result = (cameraOrientation + screenRotation) % 360
        result = (360 - result) % 360  // compensate the mirror
    } else { // back-facing
        result = (cameraOrientation - screenRotation + 360) % 360
    }
    return result
}

fun calcCameraRotation(
    isFrontCamera: Boolean,
    cameraOrientation: Int,
    phoneRotation: Int
): Int {
    return if (isFrontCamera) {
        (cameraOrientation + phoneRotation) % 360
    } else { // back-facing camera
        val landscapeFlip = if (isLandscape(phoneRotation)) 180 else 0
        (cameraOrientation + phoneRotation + landscapeFlip) % 360
    }
}

private fun isLandscape(orientationDegrees: Int): Boolean {
    return (orientationDegrees == Surface.ROTATION_90 ||
            orientationDegrees == Surface.ROTATION_270)
}
