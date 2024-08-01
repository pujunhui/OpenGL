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

#include <unistd.h>
#include "util/LogUtil.h"

#include "Triangle.h"
#include "Texture.h"

Triangle *triangle = NULL;
Texture *texture = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_texture_TextureRender_native_1onSurfaceCreated(JNIEnv *env, jobject thiz,
                                                            jobject assetManager) {
    //清空颜色
//    glClearColor(1.0, 0, 0, 1.0);

    AAssetManager *pManager = AAssetManager_fromJava(env, assetManager);
//
//    triangle = new Triangle();
//    triangle->init(pManager);

    texture = new Texture();
    texture->init(pManager);
    texture->initTexture(pManager);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_texture_TextureRender_native_1onSurfaceChanged(JNIEnv *env, jobject thiz,
                                                            jint width, jint height) {
    //设置窗口大小
    glViewport(0, 0, width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_texture_TextureRender_native_1onDrawFrame(JNIEnv *env, jobject thiz) {
    //清空颜色缓冲区或深度缓冲区
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//    triangle->draw();
    texture->draw();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_pujh_opengl_texture_TextureRender_native_1onDestroy(JNIEnv *env, jobject thiz) {
//    delete triangle;
//    triangle = NULL;

    delete texture;
    texture = NULL;
}