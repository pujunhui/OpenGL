#include <jni.h>
#include <GLES/gl.h>
#include <GLES2/gl2.h>

#include "CCNDKLogDef.h"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_pujh_opengl_CCGLRender_ndkInitGL(JNIEnv *env, jobject obj) {
    glClearColor(0.0,0.0,0.0,1.0);
    glClearDepthf(1.0);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
}

void Java_com_pujh_opengl_CCGLRender_ndkPaintGL(JNIEnv *env, jobject obj) {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity(); //重置当前的矩阵为单位矩阵
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
}

#ifdef __cplusplus
}
#endif