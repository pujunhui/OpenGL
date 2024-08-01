package com.pujh.opengl.basic.model

import android.opengl.GLES30
import com.pujh.opengl.basic.Model
import com.pujh.opengl.util.loadProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Triangle : Model {

    private val vertexShaderCode = """
        attribute vec4 aPosition;
        void main() {
            gl_Position = aPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    private val triangleCoords = floatArrayOf(
        0.0f, 0.5f, 0.0f, //顶部
        -0.5f, -0.5f, 0.0f, //左下角
        0.5f, -0.5f, 0.0f, //右下角
    )

    private val color = floatArrayOf(0.5f, 0.5f, 0.5f, 1.0f)

    private lateinit var vertexBuffer: ByteBuffer
    private var program = 0
    private var positionHandle = 0
    private var colorHandle = 0

    override fun init() {
        program = loadProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES30.glGetAttribLocation(program, "aPosition")
        colorHandle = GLES30.glGetUniformLocation(program, "vColor")

        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asFloatBuffer().put(triangleCoords).position(0)
            }
    }

    override fun draw() {
        //使用Program
        GLES30.glUseProgram(program)

        //将顶点属性与顶点缓冲对象关联起来
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glUniform4fv(colorHandle, 1, color, 0)

        //使能顶点属性数组
        GLES30.glEnableVertexAttribArray(positionHandle)

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangleCoords.size / 3)

        GLES30.glDisableVertexAttribArray(positionHandle)
    }

    override fun destroy() {
        vertexBuffer.clear()

        GLES30.glDeleteProgram(program)
    }
}