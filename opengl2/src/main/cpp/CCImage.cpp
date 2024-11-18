//
// Created by chenchao on 2021/8/12.
//

#include "CCImage.h"

#define STB_IMAGE_IMPLEMENTATION
extern "C" {
#include "stb/stb_image.h"
}

#include <malloc.h>

void rgb_to_rgba(unsigned char *rgb_data, int width, int height, unsigned char *rgba_data) {
    int size = width * height;

    for (int i = 0; i < size; ++i) {
        rgba_data[i * 4 + 0] = rgb_data[i * 3 + 0]; // R
        rgba_data[i * 4 + 1] = rgb_data[i * 3 + 1]; // G
        rgba_data[i * 4 + 2] = rgb_data[i * 3 + 2]; // B
        rgba_data[i * 4 + 3] = 255; // Alpha 通道设为 255（完全不透明）
    }
}

CCImage::CCImage() : m_width(0), m_height(0), m_type(0), m_pImgData(NULL) {

}

CCImage::~CCImage() {
    if (m_pImgData) {
        free(m_pImgData);
    }
}


void CCImage::ReadFromFile(U8_t *fileName) {
    int type = 0;
    int width = 0;
    int height = 0;

    //stbi_set_flip_vertically_on_load(true);

    U8_t *picData = stbi_load((char const *) fileName, &width, &height, &type, STBI_rgb_alpha);

    int imgSize = width * height * 4;

    if (imgSize > 0 && picData != nullptr) {
        m_pImgData = (U8_t *) malloc(imgSize);
        if (m_pImgData != NULL) {
            if (type == 4) {
                memcpy(m_pImgData, picData, imgSize);
            } else if (type == 3) {
                rgb_to_rgba(picData, width, height, m_pImgData);
            }
        }
        m_width = width;
        m_height = height;
        m_type = type;
    }

    stbi_image_free(picData);
}

void CCImage::ReadFromBuffer(U8_t *dataBuff, int length) {
    int type = 0;
    int width = 0;
    int height = 0;

    stbi_set_flip_vertically_on_load(true);

    U8_t *picData = stbi_load_from_memory((U8_t const *) dataBuff, length, &width, &height, &type,
                                          0);
    if (picData == NULL) {
        LOGE("Error loading image: %s", stbi_failure_reason());
    } else {
        LOGI("Loaded image %dx%d with %d channels", width, height, type);
    }

    int imgSize = width * height * 4;

    if (imgSize > 0 && picData != nullptr) {
        m_pImgData = (U8_t *) malloc(imgSize);
        if (m_pImgData != NULL) {
            if (type == 4) {
                memcpy(m_pImgData, picData, imgSize);
            } else if (type == 3) {
                rgb_to_rgba(picData, width, height, m_pImgData);
            }
        }

        m_width = width;
        m_height = height;
        m_type = type;
    }

    stbi_image_free(picData);
}


int CCImage::GetWidth() const {
    return m_width;
}

int CCImage::GetHeight() const {
    return m_height;
}

int CCImage::GetType() const {
    return m_type;
}

U8_t *CCImage::GetData() const {
    return m_pImgData;
}