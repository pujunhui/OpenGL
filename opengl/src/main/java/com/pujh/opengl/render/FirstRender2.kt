package com.pujh.opengl.render

import android.content.Context
import android.content.res.AssetManager
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FirstRender2(
    private val context: Context
) : GLSurfaceView.Renderer {
    init {
        System.loadLibrary("opengl")
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        ndkInitGL()
        val assetManager = context.assets
        ndkReadResourceFile(assetManager, "rabit.png")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        ndkResizeGL(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        ndkPaintGL()
    }

    private external fun ndkInitGL()
    private external fun ndkPaintGL()
    private external fun ndkResizeGL(width: Int, height: Int)
    private external fun ndkReadResourceFile(assetManager: AssetManager, fileName: String): Int
}