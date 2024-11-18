//
// Created by chenchao on 2021/8/12.
//

#include "CCGLTexture.h"

#include <malloc.h>
#include <string.h>

CCGLTexture::CCGLTexture() : m_texID(-1) {

}

CCGLTexture::~CCGLTexture() {

}

GLuint CCGLTexture::GetTextureID() {
    return m_texID;
}

GLuint CCGLTexture::CreateGLTextureFromFile(AAssetManager *assetManager, const char *fileName) {
    m_texID = generateTexture(assetManager, fileName);
    return m_texID;
}

GLuint CCGLTexture::generateTexture(AAssetManager *assetManager, const char *fileName) {
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

GLuint CCGLTexture::createOpenGLTexture(CCImage *pImg) {
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