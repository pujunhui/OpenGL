package com.pujh.opengl.basic.model

import android.opengl.GLES30
import android.opengl.Matrix
import com.pujh.opengl.basic.Model
import com.pujh.opengl.util.loadProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * VBO
 * Vertex Buffer Object顶点缓冲对象
 * 用于在GPU中存储数据，加速渲染的过程
 */
class TriangleVBO : Model {

    private val vertexShaderCode = """
        uniform mat4 uMatrix;
        attribute vec4 aPosition;
        void main() {
            gl_Position = uMatrix * aPosition;
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

    private var program = 0
    private var positionHandle = 0
    private var colorHandle = 0

    private var matrixHandle = 0
    private val translateMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
        Matrix.scaleM(this, 0, 0.5f, 0.5f, 1f)
    }

    private val vboIds = IntArray(1)

    override fun init() {
        program = loadProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES30.glGetAttribLocation(program, "aPosition")
        matrixHandle = GLES30.glGetUniformLocation(program, "uMatrix")
        colorHandle = GLES30.glGetUniformLocation(program, "vColor")

        val vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asFloatBuffer().put(triangleCoords).position(0)
            }

        //创建VBO
        GLES30.glGenBuffers(1, vboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            vertexBuffer.capacity(),
            vertexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        //解绑VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        vertexBuffer.clear()
    }


    override fun draw() {
        //使用Program
        GLES30.glUseProgram(program)

        //绑定VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])

        //将顶点属性与顶点缓冲对象关联起来
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, 0)
        GLES30.glUniform4fv(colorHandle, 1, color, 0)

        //设置变换矩阵
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, translateMatrix, 0)

        //使能顶点属性数组
        GLES30.glEnableVertexAttribArray(positionHandle)

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, triangleCoords.size / 3)

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
    }

    override fun destroy() {
        GLES30.glDeleteBuffers(1, vboIds, 0)

        GLES30.glDeleteProgram(program)
    }
}