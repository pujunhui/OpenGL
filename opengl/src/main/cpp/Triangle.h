//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_TRIANGLE_H
#define OPENGL_TRIANGLE_H

#include <GLES3/gl3.h>
#include <android/asset_manager.h>

class Triangle {
public:
    Triangle();
    ~Triangle();
    void init(AAssetManager *pManager);
    void draw();

private:
    GLuint program;
};

#endif //OPENGL_TRIANGLE_H
