//
// Created by iDste-PC on 2024-07-30.
//

#include "Preview.h"

#include <GLES3/gl3ext.h>
#include <EGL/egl.h>
#include <GLES2/gl2ext.h>

#include <malloc.h>
#include "util/GLUtil.h"
#include "util/AAssetUtil.h"
#include "util/LogUtil.h"


Preview::Preview() {

}

Preview::~Preview() {
    //删除program
    deleteProgram(program);
}

void Preview::init(AAssetManager *pManager) {
    //获取顶点着色器代码
    char *vShaderSource;
    readAssetFile(pManager, "camera/vShader.vert", &vShaderSource);

    //获取片元着色器代码
    char *fShaderSource;
    readAssetFile(pManager, "camera/fShader.frag", &fShaderSource);

    program = createProgram(vShaderSource, fShaderSource);
    textureLocation = glGetUniformLocation(program, "s_TextureMap");

    free(vShaderSource);
    free(fShaderSource);
}

void Preview::initTexture(AAssetManager *pManager) {
//    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
//
//    glUniform1i(textureLocation, 0);
}

void Preview::draw() {
    //顶点数据
    GLfloat vVertices[] = {
            1.0f, 1.0f, 0.0f,  // Position 0
            -1.0f, 1.0f, 0.0f,  // Position 1
            -1.0f, -1.0f, 0.0f,  // Position 2
            1.0f, -1.0f, 0.0f,  // Position 3
    };

    //绘制Texture纹理位置信息
    GLfloat textureCoords[] = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    //绘制顶点的信息
    GLushort indices[] = {0, 1, 2, 0, 2, 3};

    if (program == 0) {
        return;
    }

    //使用Program
    glUseProgram(program);

    //加载顶点数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), vVertices);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), textureCoords);
    //启用顶点数据
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, texId);


    //绘制三角形
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
}