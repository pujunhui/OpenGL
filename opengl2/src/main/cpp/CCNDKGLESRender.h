//
// Created by chenchao on 2021/8/12.
//

#ifndef CCOPENGLES_CCNDKGLESRENDER_H
#define CCOPENGLES_CCNDKGLESRENDER_H


#include <android/asset_manager.h>
#include <GLES/gl.h>
#include "CCGLCommonDef.h"
#include "CCNDKLogDef.h"


class CCNDKGLESRender {

public:
    CCNDKGLESRender();
    ~CCNDKGLESRender();

    void InitGL();
    void PaintGL();
    void ResizeGL(int w, int h);

    void SetupAssetManager(AAssetManager *pManager);

private:
    void loadTextureResources(AAssetManager *pManager);
    void loadShaderResources(AAssetManager *pManager);

private:
    float m_angle =0.0f;
    GLuint m_texID[6];

    AAssetManager *m_pAssetManager;


};

#endif // CCOPENGLES_CCNDKGLESRENDER_H
