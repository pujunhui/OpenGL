//
// Created by iDste-PC on 2024-07-30.
//

#include "GLUtil.h"

#include <GLES3/gl3ext.h>
#include <EGL/egl.h>
#include <malloc.h>

#include "LogUtil.h"

GLuint loadShader(GLenum shaderType, const char *pSource) {
    GLuint shader = 0;
    shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char *) malloc(infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("loadShader Could not compile shader %d:\n%s\n", shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

GLuint createProgram(const char *vShaderSource, const char *fShaderSource) {
    GLuint program = 0;
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vShaderSource);
    if (!vertexShader) {
        return program;
    }
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fShaderSource);
    if (!fragmentShader) {
        return program;
    }
    program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        glAttachShader(program, fragmentShader);
        checkGlError("glAttachShader");
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);

        glDetachShader(program, vertexShader);
        glDeleteShader(vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(fragmentShader);
        if (linkStatus != GL_TRUE) {
            GLint infoLen = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char *) malloc(infoLen);
                if (buf) {
                    glGetProgramInfoLog(program, infoLen, NULL, buf);
                    LOGE("createProgram Could not link program:\n%s\n", buf);
                    free(buf);
                }
                glDeleteProgram(program);
                program = 0;
            }
        }
    }
    LOGE("createProgram program = %d\n", program);
    return program;
}

void deleteProgram(GLuint &program) {
    if (program) {
        glUseProgram(0);
        glDeleteProgram(program);
        program = 0;
    }
}

void checkGlError(const char *pGLOperation) {
    GLenum error = glGetError();
    while (error) {
        LOGE("CheckGLError GL Operation %s() glError (0x%x)\n", pGLOperation, error);
        error = glGetError();
    }
}
