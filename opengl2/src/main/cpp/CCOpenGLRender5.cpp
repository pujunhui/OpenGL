/**
 * 绘制纹理图片
 */

#include <jni.h>
#include <GLES/gl.h>
#include <GLES2/gl2.h>

#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>

#include "CCNDKLogDef.h"
#include "CCGLCommonDef.h"
#include "glm/mat4x4.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "CCImage.h"

#ifdef __cplusplus
extern "C" {
#endif

GLuint m_texID;

GLuint createOpenGLTexture(CCImage *pImg);

void Java_com_pujh_opengl_CCGLRender2_ndkInitGL(JNIEnv *env, jobject obj) {
    glClearColor(0.0, 0.0, 0.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

void Java_com_pujh_opengl_CCGLRender2_ndkPaintGL(JNIEnv *env, jobject obj) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    //glCullFace(GL_BACK);

    CCFloat5 planVertices[] = {
            {-1.0f, -1.0f, 1.0f, 0, 0},
            {-1.0f, 1.0f,  1.0f, 0, 1},
            {1.0f,  -1.0f, 1.0f, 1, 0},
            {1.0f,  1.0f,  1.0f, 1, 1},
    };

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    glVertexPointer(3, GL_FLOAT, sizeof(CCFloat5), planVertices);
    glTexCoordPointer(2, GL_FLOAT, sizeof(CCFloat5), &planVertices[0].u);

    glBindTexture(GL_TEXTURE_2D, m_texID);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

void
Java_com_pujh_opengl_CCGLRender2_ndkResizeGL(JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    glOrthof(-1, 1, -1, 1, 0.1, 1000.0);
    //glFrustumf(-1,1,-1,1,0.1,1000.0);
}

int Java_com_pujh_opengl_CCGLRender2_ndkReadResourceFile
        (JNIEnv *env, jobject obj, jobject assetManager, jstring fName) {

    AAssetManager *mAssetManager = AAssetManager_fromJava(env, assetManager);
    if (NULL == mAssetManager) {
        LOGF("assetManager is NULL");
        return -1;
    }
    const char *fileName = env->GetStringUTFChars(fName, 0);
    if (NULL == fileName) {
        LOGF("fileName is NULL");
        return -1;
    }
    LOGD ("FileName is %s", fileName);
    AAsset *asset = AAssetManager_open(mAssetManager, fileName, AASSET_MODE_UNKNOWN);
    if (NULL == asset) {
        LOGF("asset is NULL");
        return -1;
    }
    off_t bufferSize = AAsset_getLength(asset);
    LOGD("buffer size is %ld", bufferSize);

    unsigned char *imgBuff = (unsigned char *) malloc(bufferSize + 1);
    if (NULL == imgBuff) {
        LOGF("imgBuff alloc failed");
        return -1;
    }
    memset(imgBuff, 0, bufferSize + 1);
    int readLen = AAsset_read(asset, imgBuff, bufferSize);
    LOGD("Picture read: %d", readLen);

    CCImage *glImage = new CCImage();
    glImage->ReadFromBuffer(imgBuff, readLen);
    m_texID = createOpenGLTexture(glImage);

    delete glImage;

    if (imgBuff) {
        free(imgBuff);
        imgBuff = NULL;
    }

    AAsset_close(asset);
    env->ReleaseStringUTFChars(fName, fileName);

    return 0;
}

GLuint createOpenGLTexture(CCImage *pImg) {
    if (pImg == NULL) {
        return -1;
    }

    GLuint textureID;
    glEnable(GL_TEXTURE_2D);
    glGenTextures(1, &textureID);//产生纹理索引
    glBindTexture(GL_TEXTURE_2D, textureID);//绑定纹理索引，之后的操作都针对当前纹理索引

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);//指当纹理图象被使用到一个大于它的形状上时
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//指当纹理图象被使用到一个小于或等于它的形状上时
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, pImg->GetWidth(), pImg->GetHeight(), 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, pImg->GetData());//指定参数，生成纹理

    return textureID;
}

#ifdef __cplusplus
}
#endif