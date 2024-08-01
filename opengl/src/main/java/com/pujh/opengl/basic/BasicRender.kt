package com.pujh.opengl.basic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BasicRender(
    private val model: Model
) : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        model.init()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        model.sizeChange(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        model.draw()
    }

    fun onDestroy() {
        model.destroy()
    }
}