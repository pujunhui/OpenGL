package com.pujh.opengl.util

import android.graphics.Bitmap
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES30.GL_CLAMP_TO_EDGE
import android.opengl.GLES30.GL_COMPILE_STATUS
import android.opengl.GLES30.GL_FRAGMENT_SHADER
import android.opengl.GLES30.GL_LINEAR
import android.opengl.GLES30.GL_LINK_STATUS
import android.opengl.GLES30.GL_NEAREST
import android.opengl.GLES30.GL_REPEAT
import android.opengl.GLES30.GL_RGBA
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES30.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES30.GL_TEXTURE_WRAP_S
import android.opengl.GLES30.GL_TEXTURE_WRAP_T
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glAttachShader
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glCompileShader
import android.opengl.GLES30.glCreateProgram
import android.opengl.GLES30.glCreateShader
import android.opengl.GLES30.glDeleteProgram
import android.opengl.GLES30.glDeleteShader
import android.opengl.GLES30.glDeleteTextures
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGenerateMipmap
import android.opengl.GLES30.glGetProgramInfoLog
import android.opengl.GLES30.glGetProgramiv
import android.opengl.GLES30.glGetShaderInfoLog
import android.opengl.GLES30.glGetShaderiv
import android.opengl.GLES30.glLinkProgram
import android.opengl.GLES30.glShaderSource
import android.opengl.GLES30.glTexParameteri
import android.opengl.GLUtils
import android.util.Log

fun loadShader(type: Int, shaderSrc: String): Int {
    val shader = glCreateShader(type)
    if (shader == 0) {
        Log.e("chao", "compile shader == 0")
        return 0
    }
    glShaderSource(shader, shaderSrc)
    glCompileShader(shader)
    val compileStatus = IntArray(1)
    glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
        val log = glGetShaderInfoLog(shader)
        Log.e("chao", "glGetShaderiv fail $log")
        glDeleteShader(shader)
        return 0
    }
    return shader
}

fun loadProgram(vShaderSrc: String, fShaderSrc: String): Int {
    //创建Shader
    val vShader: Int = loadShader(GL_VERTEX_SHADER, vShaderSrc)
    val fShader: Int = loadShader(GL_FRAGMENT_SHADER, fShaderSrc)
    val program = linkProgram(vShader, fShader)
    //链接完成后，就可以释放Shader
    glDeleteShader(vShader)
    glDeleteShader(fShader)
    return program
}

fun linkProgram(vShader: Int, fShader: Int): Int {
    //创建Program
    val program = glCreateProgram()
    if (program == 0) {
        Log.e("chao", "program == 0")
        return 0
    }

    //将Shader附着到Program
    glAttachShader(program, vShader)
    glAttachShader(program, fShader)

    //链接Program
    glLinkProgram(program)
    val linkStatus = IntArray(1)
    glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
    if (linkStatus[0] == 0) {
        val log = glGetProgramInfoLog(program)
        Log.e("chao", "linkProgram fail $log")
        glDeleteProgram(program)
        return 0
    }
    return program
}

fun createTexture(bitmap: Bitmap): Int {
    val textureIds = IntArray(1)
    glGenTextures(1, textureIds, 0)
    glBindTexture(GL_TEXTURE_2D, textureIds[0])

    // 为当前绑定的纹理对象设置环绕、过滤方式
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

    GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0)
    glGenerateMipmap(GL_TEXTURE_2D)

    glBindTexture(GL_TEXTURE_2D, 0)
    return textureIds[0]
}

fun createOESTexture(): Int {
    // 创建一个用于存储纹理ID的数组
    val textureIds = IntArray(1)
    // 生成一个纹理对象，并将纹理ID存储到数组中
    glGenTextures(1, textureIds, 0)
    // 将当前纹理绑定到OpenGL ES的纹理目标（外部OES纹理）
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureIds[0])
    // 设置纹理S轴的包裹模式为GL_CLAMP_TO_EDGE，即超出边界的纹理坐标会被截取到边界上的纹素
    glTexParameteri(
        GL_TEXTURE_EXTERNAL_OES,
        GL_TEXTURE_WRAP_S,
        GL_CLAMP_TO_EDGE
    )
    // 设置纹理T轴的包裹模式为GL_CLAMP_TO_EDGE
    glTexParameteri(
        GL_TEXTURE_EXTERNAL_OES,
        GL_TEXTURE_WRAP_T,
        GL_CLAMP_TO_EDGE
    )
    // 设置纹理缩小过滤器为GL_NEAREST，即使用最近邻采样的方式进行纹理缩小
    glTexParameteri(
        GL_TEXTURE_EXTERNAL_OES,
        GL_TEXTURE_MIN_FILTER,
        GL_NEAREST
    )
    // 设置纹理放大过滤器为GL_NEAREST
    glTexParameteri(
        GL_TEXTURE_EXTERNAL_OES,
        GL_TEXTURE_MAG_FILTER,
        GL_NEAREST
    )
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0)
    return textureIds[0]
}

fun deleteTexture(textureId: Int) {
    glDeleteTextures(1, intArrayOf(textureId), 0)
}