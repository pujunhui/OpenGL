package com.pujh.opengl.camera

import android.app.Activity
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.view.Surface
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.impl.CameraInternal
import com.pujh.opengl.camera.filter.JavaFilter
import com.pujh.opengl.util.createOESTexture
import com.pujh.opengl.util.deleteTexture
import com.pujh.opengl.util.getDisplayRotation
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(
    private val activity: Activity,
    private val glSurfaceView: GLSurfaceView
) : GLSurfaceView.Renderer, Preview.SurfaceProvider {
    private val filter = JavaFilter(activity)

    private val oesTextureId = createOESTexture()
    private val surfaceTexture = SurfaceTexture(oesTextureId)
    private val executor = Executors.newSingleThreadExecutor()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        filter.init(oesTextureId)
        surfaceTexture.setOnFrameAvailableListener {
            glSurfaceView.requestRender()
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        filter.windowSizeChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        surfaceTexture.updateTexImage()
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        filter.draw()
    }

    override fun onSurfaceRequested(request: SurfaceRequest) {
        val size = request.resolution
        surfaceTexture.setDefaultBufferSize(size.width, size.height)
        filter.streamSizeChanged(size.width, size.height)
        val surface = Surface(surfaceTexture)
        request.provideSurface(surface, executor) {
            surface.release()
        }
    }

    fun onDestroy() {
        filter.destroy()
        surfaceTexture.release()
        deleteTexture(oesTextureId)
    }

    private fun calcDisplayRotation(camera: CameraInternal): Int {
        val orientation = camera.cameraInfo.sensorRotationDegrees
        val rotation = activity.getDisplayRotation()
        var result: Int
        if (camera.isFrontFacing) {
            result = (orientation + rotation) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else { // back-facing
            result = (orientation - rotation + 360) % 360
        }
        return result
    }
}