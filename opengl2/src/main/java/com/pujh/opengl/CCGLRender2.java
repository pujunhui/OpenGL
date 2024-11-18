package com.pujh.opengl;


import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class CCGLRender2 implements GLSurfaceView.Renderer {

    CCGLRender2(Context ctx) {
        m_glContex = ctx;
    }

    private final Context m_glContex;

    private native void ndkInitGL();

    private native void ndkPaintGL();

    private native void ndkResizeGL(int width, int height);

    private native int ndkReadResourceFile(AssetManager assetManager, String fileName);


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        AssetManager assetManager = m_glContex.getAssets();
        ndkInitGL();
        ndkReadResourceFile(assetManager, "rabit.png");
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