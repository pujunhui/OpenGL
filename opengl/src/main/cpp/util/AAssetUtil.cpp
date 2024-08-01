//
// Created by iDste-PC on 2024-07-30.
//

#include "AAssetUtil.h"
#include <android/asset_manager_jni.h>
#include <malloc.h>
#include "LogUtil.h"

int readAssetFile(AAssetManager *pManager, const char *filename, char **output) {
    AAsset *pAsset = AAssetManager_open(pManager, filename, AASSET_MODE_BUFFER);
    size_t count = AAsset_getLength(pAsset);
    char *buf = (char *) malloc(count);
    if (buf) {
        AAsset_read(pAsset, buf, count);
        LOGD("read asset file: %s\n%s", filename, buf);
        *output = buf;
    } else {
        count = -1;
        *output = NULL;
        LOGE("read asset file filed: %s", filename);
    }
    AAsset_close(pAsset);
    return count;
}
