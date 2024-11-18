//
// Created by chenchao on 2021/8/12.
//

#ifndef CCOPENGLES_CCGLTEXTURE_H
#define CCOPENGLES_CCGLTEXTURE_H

#include <GLES/gl.h>
#include <android/asset_manager.h>
#include "CCGLCommonDef.h"
#include "CCNDKLogDef.h"
#include "CCImage.h"

class CCGLTexture {

public:
    CCGLTexture();
    ~CCGLTexture();

    GLuint  GetTextureID();
    GLuint  CreateGLTextureFromFile(AAssetManager *assetManager, const char* fileName);

private:
    GLuint  generateTexture(AAssetManager *assetManager, const char* fileName);
    GLuint  createOpenGLTexture(CCImage* pImg);

private:
    GLuint m_texID;
};


#endif //CCOPENGLES_CCGLTEXTURE_H
