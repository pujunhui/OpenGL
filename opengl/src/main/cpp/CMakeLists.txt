cmake_minimum_required(VERSION 3.22.1)

project("opengl")

#引入stbimage
set(STBIMAGE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/stb)
include_directories(${STBIMAGE_DIR})

add_library(${CMAKE_PROJECT_NAME} SHARED
        TextureRender.cpp
        CameraRender.cpp
        Triangle.cpp
        Texture.cpp
        Preview.cpp
        util/GLUtil.cpp
        util/AAssetUtil.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        GLESv3
        EGL
        android
        log)