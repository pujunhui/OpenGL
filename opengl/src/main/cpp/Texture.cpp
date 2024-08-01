//
// Created by iDste-PC on 2024-07-30.
//

#include "Texture.h"

#include <GLES3/gl3ext.h>
#include <EGL/egl.h>
#include <malloc.h>
#include "util/GLUtil.h"
#include "util/AAssetUtil.h"

//引入stb_image
#define STB_IMAGE_IMPLEMENTATION
extern "C" {
#include "stb/stb_image.h"
}


Texture::Texture() {

}

Texture::~Texture() {
    //删除program
    deleteProgram(program);
}

void Texture::init(AAssetManager *pManager) {
    //获取顶点着色器代码
    char *vShaderSource;
    readAssetFile(pManager, "texture/vShader.vert", &vShaderSource);

    //获取片元着色器代码
    char *fShaderSource;
    readAssetFile(pManager, "texture/fShader.frag", &fShaderSource);

    program = createProgram(vShaderSource, fShaderSource);

    textureLocation = glGetUniformLocation(program, "s_TextureMap");

    free(vShaderSource);
    free(fShaderSource);
}

void Texture::initTexture(AAssetManager *pManager) {
    glEnable(GL_TEXTURE_2D);
    //产生纹理索引
    glGenTextures(1, &texId);
    glBindTexture(GL_TEXTURE_2D, texId);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);//指当纹理图象被使用到一个大于它的形状上时
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//指当纹理图象被使用到一个小于或等于它的形状上时
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    //获取图片数据
    char *image;
    int count = readAssetFile(pManager, "img.png", &image);

    // 加载并生成纹理
    unsigned char *data = stbi_load_from_memory((const stbi_uc *) image, count,
                                                &width, &height, &channels, 0);
    free(image);

    if (data) {
        if (channels == 3) {
            //字节对齐
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE,
                         data);
        } else if (channels == 4) {
            //字节对齐
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                         data);
        }
    }
    stbi_image_free(data);
}

void Texture::draw() {
    //顶点数据
    static GLfloat vVertices[] = {
            -1.0f, 0.5f, 0.0f,  // Position 0
            -1.0f, -0.5f, 0.0f,  // Position 1
            1.0f, -0.5f, 0.0f,  // Position 2
            1.0f, 0.5f, 0.0f,  // Position 3
    };

    //绘制Texture纹理位置信息
    static GLfloat textureCoords[] = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    //绘制顶点的信息
    static GLushort indices[] = {0, 1, 2, 0, 2, 3};

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
    glBindTexture(GL_TEXTURE_2D, texId);

    glUniform1i(textureLocation, 0);

    //绘制三角形
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
}