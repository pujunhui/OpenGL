//
// Created by iDste-PC on 2024-07-30.
//
#include <jni.h>
#include <string>

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>
#include <EGL/egl.h>

#include <pthread.h>
#include <unistd.h>
#include "util/LogUtil.h"

#include "Triangle.h"
#include "Texture.h"

static JavaVM *gVm;
EGLDisplay gDisplay;
EGLContext gContext;
EGLSurface gSurface;

void *runLoop(void *) {
    // running on bind thread.
    LOGD("start thread.");
    JNIEnv *env = nullptr;
    gVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    gVm->AttachCurrentThread(&env, nullptr);

    eglMakeCurrent(gDisplay, gSurface, gSurface, gContext);
//    GLRender render;
    GLuint glProgram = 0;//render.createGlProgram();
    GLuint glPosition = glGetAttribLocation(glProgram, "aPosition");


    int count = 1;
    while (count++ < 1000) {
        LOGD("run loop");
        if (count % 2 == 0) {
            glClearColor(1.0, 0, 0, 1.0);
        } else {
            glClearColor(0, 1.0, 1.0, 1);
        }
//        render.checkGlError("clearColor");
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
//        render.checkGlError("clearGLError");
        glUseProgram(glProgram);
        glEnableVertexAttribArray(glPosition);
//        glVertexAttribPointer(glPosition, 2, GL_FLOAT, GL_FALSE, 0, render.getData());
        glDrawArrays(GL_TRIANGLES, 0, 3);

        //交换缓冲
        eglSwapBuffers(gDisplay, gSurface);
        sleep(1);
    }

    gVm->DetachCurrentThread();
    return nullptr;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pujh_opengl_NativeRenderer_init(JNIEnv *env, jobject thiz, jobject surface) {
    //获取ANativeWindow
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    //给ANativeWindow设置缓存
    ANativeWindow_setBuffersGeometry(window, 0, 0, AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM);
    ANativeWindow_acquire(window);

    //获取EGLDisplay
    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (display == EGL_NO_DISPLAY) {
        LOGE("egl have not got display.");
        return JNI_FALSE;
    }
    if (eglInitialize(display, 0, 0) != EGL_TRUE) {
        LOGE("egl Initialize failed.%d", eglGetError());
        return JNI_FALSE;
    }
    gDisplay = display;
    //创建EGLContext
    const EGLint atrribs[] = {
            EGL_BUFFER_SIZE, 32,
            EGL_ALPHA_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };

    EGLConfig eglConfig;
    EGLint numOfEglConfig;
    if (eglChooseConfig(display, atrribs, &eglConfig, 1, &numOfEglConfig) != EGL_TRUE) {
        LOGE("egl choose config failed.%d,", eglGetError());
        return JNI_FALSE;
    }
    EGLint attributes[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};
    gContext = eglCreateContext(display, eglConfig, nullptr, attributes);
    if (!gContext) {
        LOGE("eglCreateContext failed.");
        return JNI_FALSE;
    }

    //创建EGLSurface
    gSurface = eglCreateWindowSurface(display, eglConfig, window, 0);
    if (!gSurface) {
        return JNI_FALSE;
    }

    //在渲染线程关联GLContext和GLSurface和GLDisplay
    eglMakeCurrent(gDisplay, gSurface, gSurface, gContext);

    // start a thread.
    pthread_t thread;
    pthread_create(&thread, nullptr, runLoop, nullptr);
}
