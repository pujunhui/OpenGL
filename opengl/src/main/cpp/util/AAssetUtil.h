//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_AASSETUTIL_H
#define OPENGL_AASSETUTIL_H

#include <android/asset_manager.h>

int readAssetFile(AAssetManager *pManager, const char *filename, char **output);

#endif //OPENGL_AASSETUTIL_H
