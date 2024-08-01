//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_GLUTIL_H
#define OPENGL_GLUTIL_H

#include <GLES3/gl3.h>

GLuint loadShader(GLenum shaderType, const char *pSource);

GLuint createProgram(const char *vShaderSource, const char *fShaderSource);

void deleteProgram(GLuint &program);

void checkGlError(const char *pGLOperation);

#endif //OPENGL_GLUTIL_H
