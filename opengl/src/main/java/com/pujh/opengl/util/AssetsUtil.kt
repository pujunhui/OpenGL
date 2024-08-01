package com.pujh.opengl.util

import android.content.Context
import java.nio.charset.StandardCharsets

fun Context.loadAssets(name: String): String {
    assets.open(name).use { input ->
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        return String(buffer, StandardCharsets.UTF_8)
    }
}
