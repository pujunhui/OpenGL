package com.pujh.opengl.basic

import android.opengl.GLES30

interface Model {
    fun init()

    fun sizeChange(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    fun draw()

    fun destroy()

}