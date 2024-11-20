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
#include "util/CCGLUtil.h"

#ifdef __cplusplus
extern "C" {
#endif

GLuint m_texID;

GLuint createOpenGLTexture(CCImage *pImg);

void Java_com_pujh_opengl_CCGLRender2_ndkInitGL(JNIEnv *env, jobject obj, jobject assetManager) {
    glClearColor(0.0, 0.0, 0.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    AAssetManager *mAssetManager = AAssetManager_fromJava(env, assetManager);
    if (NULL == mAssetManager) {
        LOGF("assetManager is NULL");
        return;
    }
    m_texID = readImageFileAndCreateGLTexture(mAssetManager, "rabit.png");
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

#ifdef __cplusplus
}
#endif