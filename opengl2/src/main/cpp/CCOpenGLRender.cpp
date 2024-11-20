#include <jni.h>

#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>

#include "CCNDKLogDef.h"
#include "CCGLCommonDef.h"
#include "CCNDKGLESRender.h"

#ifdef __cplusplus
extern "C" {
#endif

CCNDKGLESRender m_ndkGLESRender;

void Java_com_pujh_opengl_CCGLRender2_ndkInitGL(JNIEnv *env, jobject obj, jobject assetManager) {
    AAssetManager *astManager = AAssetManager_fromJava(env, assetManager);
    if (NULL != astManager) {
        m_ndkGLESRender.SetupAssetManager(astManager);
    }
    m_ndkGLESRender.InitGL();
}

void Java_com_pujh_opengl_CCGLRender2_ndkPaintGL(JNIEnv *env, jobject obj) {
    m_ndkGLESRender.PaintGL();
}

void
Java_com_pujh_opengl_CCGLRender2_ndkResizeGL(JNIEnv *env, jobject obj, jint width, jint height) {
    m_ndkGLESRender.ResizeGL(width, height);
}

#ifdef __cplusplus
}
#endif