package com.pujh.opengl;


import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CCGLRender implements GLSurfaceView.Renderer {

    private native void ndkInitGL();

    private native void ndkPaintGL();

    private native void ndkResizeGL(int width, int height);

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ndkInitGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        ndkResizeGL(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        ndkPaintGL();
    }

    static {
        System.loadLibrary("CCOpenGLRender");
    }
}
