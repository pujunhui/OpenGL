/**
 * 绘制立方体，并通过gml库进行旋转
 */

#include <jni.h>
#include <GLES/gl.h>
#include <GLES2/gl2.h>

#include "CCNDKLogDef.h"
#include "CCGLCommonDef.h"
#include "glm/mat4x4.hpp"
#include "glm/gtc/type_ptr.hpp"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_pujh_opengl_CCGLRender_ndkInitGL(JNIEnv *env, jobject obj) {
    glClearColor(0.0, 0.0, 0.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

float m_angle = 0.0f;

void Java_com_pujh_opengl_CCGLRender_ndkPaintGL(JNIEnv *env, jobject obj) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity(); //重置当前的矩阵为单位矩阵

    glCullFace(GL_BACK);

    CCFloat7 cubeVert[] = {
            {-0.5f, -0.5f, -0.5f, 1.0, 0.0, 0.0, 1.0},
            {0.5f,  -0.5f, -0.5f, 1.0, 0.0, 0.0, 1.0},
            {0.5f,  0.5f,  -0.5f, 1.0, 0.0, 0.0, 1.0},
            {0.5f,  0.5f,  -0.5f, 1.0, 0.0, 0.0, 1.0},
            {-0.5f, 0.5f,  -0.5f, 1.0, 0.0, 0.0, 1.0},
            {-0.5f, -0.5f, -0.5f, 1.0, 0.0, 0.0, 1.0},

            {-0.5f, -0.5f, 0.5f,  0.0, 1.0, 0.0, 1.0},
            {0.5f,  -0.5f, 0.5f,  0.0, 1.0, 0.0, 1.0},
            {0.5f,  0.5f,  0.5f,  0.0, 1.0, 0.0, 1.0},
            {0.5f,  0.5f,  0.5f,  0.0, 1.0, 0.0, 1.0},
            {-0.5f, 0.5f,  0.5f,  0.0, 1.0, 0.0, 1.0},
            {-0.5f, -0.5f, 0.5f,  0.0, 1.0, 0.0, 1.0},

            {-0.5f, 0.5f,  0.5f,  0.0, 0.0, 1.0, 1.0},
            {-0.5f, 0.5f,  -0.5f, 0.0, 0.0, 1.0, 1.0},
            {-0.5f, -0.5f, -0.5f, 0.0, 0.0, 1.0, 1.0},
            {-0.5f, -0.5f, -0.5f, 0.0, 0.0, 1.0, 1.0},
            {-0.5f, -0.5f, 0.5f,  0.0, 0.0, 1.0, 1.0},
            {-0.5f, 0.5f,  0.5f,  0.0, 0.0, 1.0, 1.0},

            {0.5f,  0.5f,  0.5f,  1.0, 0.0, 1.0, 1.0},
            {0.5f,  0.5f,  -0.5f, 1.0, 0.0, 1.0, 1.0},
            {0.5f,  -0.5f, -0.5f, 1.0, 0.0, 1.0, 1.0},
            {0.5f,  -0.5f, -0.5f, 1.0, 0.0, 1.0, 1.0},
            {0.5f,  -0.5f, 0.5f,  1.0, 0.0, 1.0, 1.0},
            {0.5f,  0.5f,  0.5f,  1.0, 0.0, 1.0, 1.0},

            {-0.5f, -0.5f, -0.5f, 0.0, 1.0, 1.0, 1.0},
            {0.5f,  -0.5f, -0.5f, 0.0, 1.0, 1.0, 1.0},
            {0.5f,  -0.5f, 0.5f,  0.0, 1.0, 1.0, 1.0},
            {0.5f,  -0.5f, 0.5f,  0.0, 1.0, 1.0, 1.0},
            {-0.5f, -0.5f, 0.5f,  0.0, 1.0, 1.0, 1.0},
            {-0.5f, -0.5f, -0.5f, 0.0, 1.0, 1.0, 1.0},

            {-0.5f, 0.5f,  -0.5f, 1.0, 1.0, 0.0, 1.0},
            {0.5f,  0.5f,  -0.5f, 1.0, 1.0, 0.0, 1.0},
            {0.5f,  0.5f,  0.5f,  1.0, 1.0, 0.0, 1.0},
            {0.5f,  0.5f,  0.5f,  1.0, 1.0, 0.0, 1.0},
            {-0.5f, 0.5f,  0.5f,  1.0, 1.0, 0.0, 1.0},
            {-0.5f, 0.5f,  -0.5f, 1.0, 1.0, 0.0, 1.0}
    };

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);

    glVertexPointer(3, GL_FLOAT, sizeof(CCFloat7), cubeVert);
    glColorPointer(4, GL_FLOAT, sizeof(CCFloat7), &cubeVert[0].r);

    m_angle += 0.01f;

    glm::mat4x4 cubeMat;
    glm::mat4x4 cubeTransMat = glm::translate(glm::mat4(1.0f), glm::vec3(0.0f, 0.0f, -0.5));
    glm::mat4x4 cubeRotMat = glm::rotate(glm::mat4(1.0f), m_angle, glm::vec3(0.5f, 0.5f, 1.0));
    glm::mat4x4 cubeScaleMat = glm::scale(glm::mat4(1.0f), glm::vec3(0.5f, 0.4f, 0.5));
    cubeMat = cubeTransMat * cubeRotMat * cubeScaleMat;

    glLoadMatrixf(glm::value_ptr(cubeMat));

    glDrawArrays(GL_TRIANGLES, 0, 36);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);
}

void
Java_com_pujh_opengl_CCGLRender_ndkResizeGL(JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();

    glOrthof(-1, 1, -1, 1, 0.1, 1000.0);
    //glFrustumf(-1,1,-1,1,0.1,1000.0);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}

#ifdef __cplusplus
}
#endif