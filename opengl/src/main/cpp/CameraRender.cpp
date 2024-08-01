//
// Created by iDste-PC on 2024-07-30.
//
#include <jni.h>
#include <string>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>
#include <EGL/egl.h>

#include <pthread.h>
#include <unistd.h>
#include <GLES2/gl2ext.h>
#include "util/LogUtil.h"

#include "Preview.h"
#include "util/GLUtil.h"

Preview *preview = NULL;

GLuint texId;

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_camera_NativeRender_native_1onSurfaceCreated(JNIEnv *env, jobject thiz,
                                                                  jobject assetManager) {
    //清空颜色
//    glClearColor(1.0, 0, 0, 1.0);
    AAssetManager *pManager = AAssetManager_fromJava(env, assetManager);

    preview = new Preview();
    preview->init(pManager);
    preview->initTexture(pManager);
    preview->texId = texId;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_pujh_opengl_camera_NativeRender_native_1createOESTexture(JNIEnv *env, jobject thiz) {
    //创建OES纹理索引
    glGenTextures(1, &texId);
    checkGlError("glGenTextures");
    glActiveTexture(GL_TEXTURE0);
    checkGlError("glActiveTexture");
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, texId);
    checkGlError("glBindTexture");

    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    checkGlError("glTexParameter");

    return texId;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_camera_NativeRender_native_1onSurfaceChanged(JNIEnv *env, jobject thiz,
                                                                  jint width, jint height) {
    //设置窗口大小
    glViewport(0, 0, width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_camera_NativeRender_native_1onDrawFrame(JNIEnv *env, jobject thiz) {
    //清空颜色缓冲区或深度缓冲区
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    preview->draw();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_camera_NativeRender_native_1onDestroy(JNIEnv *env, jobject thiz) {
    delete preview;
    preview = NULL;
}