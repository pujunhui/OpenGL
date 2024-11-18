/**
 * 画顶点和线段
 */

#include <jni.h>
#include <GLES/gl.h>
#include <GLES2/gl2.h>

#include "CCNDKLogDef.h"
#include "CCGLCommonDef.h"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_pujh_opengl_CCGLRender_ndkInitGL(JNIEnv *env, jobject obj) {
    glClearColor(0.0, 0.0, 0.0, 1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

void Java_com_pujh_opengl_CCGLRender_ndkPaintGL(JNIEnv *env, jobject obj) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    CCFloat3 vertexTriangle[] = {
            {-0.5, 0.5,  -1},
            {-0.5, -0.5, -1},
            {0.5,  0.5,  -1},
            {0.5,  -0.5, -1}
    };

    glColor4f(1.0, 1.0, 1.0, 1.0);
    glEnableClientState(GL_VERTEX_ARRAY);
    glVertexPointer(3, GL_FLOAT, sizeof(CCFloat3), vertexTriangle);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glLineWidth(12);
//    glEnable(GL_LINE_SMOOTH);
//    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

    glDrawArrays(GL_LINE_LOOP, 0, 4);

    glPointSize(96);
//    glEnable(GL_POINT_SMOOTH);
//    glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);

    glDrawArrays(GL_POINTS, 0, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
}

void
Java_com_pujh_opengl_CCGLRender_ndkResizeGL(JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);

    // 在现代 OpenGL（OpenGL 3.0 及更高版本）中，固定功能管线被移除，因此不再直接使用 glLoadIdentity。
    // 取而代之的是，使用着色器和自定义矩阵变换。
    // 在这种情况下，开发者会使用数学库（如 GLM）来创建和操作矩阵。
    glMatrixMode(GL_PROJECTION); // 选择投影矩阵
    glLoadIdentity(); // 重置投影矩阵

    glOrthof(-1, 1, -1, 1, 0.1, 1000.0);

    glMatrixMode(GL_MODELVIEW); // 选择模型视图矩阵
    glLoadIdentity(); // 重置模型视图矩阵
}

#ifdef __cplusplus
}
#endif