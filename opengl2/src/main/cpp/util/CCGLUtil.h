//
// Created by 24415 on 2024-11-28.
//

#ifndef OPENGL_CCGLUTIL_H
#define OPENGL_CCGLUTIL_H


#include <GLES/gl.h>
#include <android/asset_manager.h>

GLuint readImageFileAndCreateGLTexture(AAssetManager *assetManager, const char *fileName);


#endif //OPENGL_CCGLUTIL_H
