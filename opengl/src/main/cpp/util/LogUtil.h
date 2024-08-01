//
// Created by iDste-PC on 2024-07-30.
//

#ifndef OPENGL_LOGUTIL_H
#define OPENGL_LOGUTIL_H

#include <android/log.h>

#ifndef LOG_TAG
#define LOG_TAG "OpenGL-Log"
#endif

#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

#endif //OPENGL_LOGUTIL_H
