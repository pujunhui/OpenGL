//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_TEXTURE_H
#define OPENGL_TEXTURE_H

#include <GLES3/gl3.h>
#include <android/asset_manager.h>

class Texture {
public:
    Texture();
    ~Texture();
    void init(AAssetManager *pManager);
    void initTexture(AAssetManager *pManager);
    void draw();

private:
    GLuint program;
    GLuint texId;
    int width, height, channels;
    GLuint textureLocation;
};


#endif //OPENGL_TEXTURE_H
