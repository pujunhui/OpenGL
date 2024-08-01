//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_PREVIEW_H
#define OPENGL_PREVIEW_H

#include <GLES3/gl3.h>
#include <android/asset_manager.h>

class Preview {
public:
    Preview();
    ~Preview();
    void init(AAssetManager *pManager);
    void initTexture(AAssetManager *pManager);
    void draw();
    GLuint texId;

private:
    GLuint program;
    int width, height, channels;
    GLint textureLocation;
};


#endif //OPENGL_PREVIEW_H
