/**
 * 在立方体上绘制纹理
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

float m_angle = 0.0f;
GLuint m_texID[6];

GLuint createOpenGLTexture(CCImage *pImg);
GLuint readImageFileAndCreateGLTexture(AAssetManager *assetManager, const char *fileName);

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

    glCullFace(GL_BACK);
    glFrontFace(GL_CW);

    CCFloat5 cubeVertexs[] = {
            {-0.5f, -0.5f, -0.5f, 0.0f, 0.0f},
            {0.5f,  -0.5f, -0.5f, 1.0f, 0.0f},
            {0.5f,  0.5f,  -0.5f, 1.0f, 1.0f},
            {0.5f,  0.5f,  -0.5f, 1.0f, 1.0f},
            {-0.5f, 0.5f,  -0.5f, 0.0f, 1.0f},
            {-0.5f, -0.5f, -0.5f, 0.0f, 0.0f},

            {-0.5f, -0.5f, 0.5f,  0.0f, 0.0f},
            {0.5f,  -0.5f, 0.5f,  1.0f, 0.0f},
            {0.5f,  0.5f,  0.5f,  1.0f, 1.0f},
            {0.5f,  0.5f,  0.5f,  1.0f, 1.0f},
            {-0.5f, 0.5f,  0.5f,  0.0f, 1.0f},
            {-0.5f, -0.5f, 0.5f,  0.0f, 0.0f},

            {-0.5f, 0.5f,  0.5f,  1.0f, 0.0f},
            {-0.5f, 0.5f,  -0.5f, 1.0f, 1.0f},
            {-0.5f, -0.5f, -0.5f, 0.0f, 1.0f},
            {-0.5f, -0.5f, -0.5f, 0.0f, 1.0f},
            {-0.5f, -0.5f, 0.5f,  0.0f, 0.0f},
            {-0.5f, 0.5f,  0.5f,  1.0f, 0.0f},

            {0.5f,  0.5f,  0.5f,  1.0f, 0.0f},
            {0.5f,  0.5f,  -0.5f, 1.0f, 1.0f},
            {0.5f,  -0.5f, -0.5f, 0.0f, 1.0f},
            {0.5f,  -0.5f, -0.5f, 0.0f, 1.0f},
            {0.5f,  -0.5f, 0.5f,  0.0f, 0.0f},
            {0.5f,  0.5f,  0.5f,  1.0f, 0.0f},

            {-0.5f, -0.5f, -0.5f, 0.0f, 1.0f},
            {0.5f,  -0.5f, -0.5f, 1.0f, 1.0f},
            {0.5f,  -0.5f, 0.5f,  1.0f, 0.0f},
            {0.5f,  -0.5f, 0.5f,  1.0f, 0.0f},
            {-0.5f, -0.5f, 0.5f,  0.0f, 0.0f},
            {-0.5f, -0.5f, -0.5f, 0.0f, 1.0f},

            {-0.5f, 0.5f,  -0.5f, 0.0f, 1.0f},
            {0.5f,  0.5f,  -0.5f, 1.0f, 1.0f},
            {0.5f,  0.5f,  0.5f,  1.0f, 0.0f},
            {0.5f,  0.5f,  0.5f,  1.0f, 0.0f},
            {-0.5f, 0.5f,  0.5f,  0.0f, 0.0f},
            {-0.5f, 0.5f,  -0.5f, 0.0f, 1.0f}
    };

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    glVertexPointer(3, GL_FLOAT, sizeof(CCFloat5), cubeVertexs);
    glTexCoordPointer(2, GL_FLOAT, sizeof(CCFloat5), &cubeVertexs[0].u);

    m_angle += 0.01f;

    glm::mat4x4 cubeMat;
    glm::mat4x4 cubeTransMat = glm::translate(glm::mat4(1.0f), glm::vec3(0.0f, 0.0f, -0.5));
    glm::mat4x4 cubeRotMat = glm::rotate(glm::mat4(1.0f), m_angle, glm::vec3(1.0f, 1.0f, 0.0));
    glm::mat4x4 cubeScaleMat = glm::scale(glm::mat4(1.0f), glm::vec3(0.4f, 0.4f, 0.5));

    cubeMat = cubeTransMat * cubeRotMat * cubeScaleMat;

    glLoadMatrixf(glm::value_ptr(cubeMat));

    //glDrawArrays(GL_TRIANGLES,0,36);
    for (int i = 0; i < 6; i++) {
        glBindTexture(GL_TEXTURE_2D, m_texID[i]);
        glDrawArrays(GL_TRIANGLES, i * 6, 6);
    }

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

    for (int i = 0; i < 6; i++) {
        char nameBuff[6];
        memset(nameBuff, 0, sizeof(nameBuff));
        sprintf(nameBuff, "%d.png", i + 1);
        nameBuff[5] = '\0';
        LOGD("Image Name:%s", nameBuff);
        m_texID[i] = readImageFileAndCreateGLTexture(mAssetManager, nameBuff);
    }

    return 0;
}

GLuint readImageFileAndCreateGLTexture(AAssetManager *assetManager, const char *fileName) {
    AAsset *asset = AAssetManager_open(assetManager, fileName, AASSET_MODE_UNKNOWN);
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
    GLuint texID = createOpenGLTexture(glImage);

    delete glImage;

    if (imgBuff) {
        free(imgBuff);
        imgBuff = NULL;
    }

    AAsset_close(asset);

    return texID;
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