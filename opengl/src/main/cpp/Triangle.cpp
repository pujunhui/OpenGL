//
// Created by iDste-PC on 2024-07-30.
//

#include "Triangle.h"

#include <GLES3/gl3ext.h>
#include <EGL/egl.h>
#include <malloc.h>
#include "util/GLUtil.h"
#include "util/AAssetUtil.h"

Triangle::Triangle() {

}

Triangle::~Triangle() {
    //删除program
    deleteProgram(program);
}

void Triangle::init(AAssetManager *pManager) {
    //获取顶点着色器代码
    char *vShaderSource;
    readAssetFile(pManager, "triangle/vShader.vert", &vShaderSource);

    //获取片元着色器代码
    char *fShaderSource;
    readAssetFile(pManager, "triangle/fShader.frag", &fShaderSource);

    program = createProgram(vShaderSource, fShaderSource);

    free(vShaderSource);
    free(fShaderSource);
}

void Triangle::draw() {
    //顶点数据
    static GLfloat vVertices[] = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };
    if (program == 0) {
        return;
    }

    //使用Program
    glUseProgram(program);

    //加载顶点数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), vVertices);
    //启用顶点数据
    glEnableVertexAttribArray(0);
    //绘制三角形
    glDrawArrays(GL_TRIANGLES, 0, 3);
}