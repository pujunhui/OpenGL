package com.pujh.opengl.basic.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import androidx.annotation.DrawableRes
import com.pujh.opengl.R
import com.pujh.opengl.basic.Model
import com.pujh.opengl.util.createTexture
import com.pujh.opengl.util.deleteTexture
import com.pujh.opengl.util.loadBitmap
import com.pujh.opengl.util.loadProgram
import com.pujh.opengl.util.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TextureRenderOrtho(
    private val context: Context
) : Model {

    private val vertexShaderCode = """
        uniform mat4 uMatrix;
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = uMatrix * aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uSampler;
        varying vec2 vTexCoord;
        void main() {
            gl_FragColor = texture2D(uSampler, vTexCoord);
        }
    """.trimIndent()

    private val triangleCoords = floatArrayOf(
        //顶点坐标            纹理坐标
        -1f, 1f, 0.0f, 0f, 0f, //左上
        -1f, -1f, 0.0f, 0f, 1f, //左下
        1f, -1f, 0.0f, 1f, 1f,//右下
        1f, 1f, 0.0f, 1f, 0f,//右上
    )

    private var program = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var samplerHandle = 0

    private var matrixHandle = 0
    private val translateMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
    }

    private val vboIds = IntArray(1)
    private val eboIds = IntArray(1)
    private val vaoIds = IntArray(1)

    private val indexes = intArrayOf(0, 1, 2, 2, 3, 0)

    private var textureId = 0

    override fun init() {
        program = loadProgram(vertexShaderCode, fragmentShaderCode)

        positionHandle = GLES30.glGetAttribLocation(program, "aPosition")
        matrixHandle = GLES30.glGetUniformLocation(program, "uMatrix")
        texCoordHandle = GLES30.glGetAttribLocation(program, "aTexCoord")
        samplerHandle = GLES30.glGetUniformLocation(program, "uSampler")

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

        val indexBuffer = ByteBuffer.allocateDirect(indexes.size * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asIntBuffer().put(indexes).position(0)
            }

        //创建EBO
        GLES30.glGenBuffers(1, eboIds, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIds[0])
        GLES30.glBufferData(
            GLES30.GL_ELEMENT_ARRAY_BUFFER,
            indexBuffer.capacity(),
            indexBuffer,
            GLES30.GL_STATIC_DRAW
        )
        //解绑EBO
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        indexBuffer.clear()

        //创建VAO
        GLES30.glGenVertexArrays(1, vaoIds, 0)
        GLES30.glBindVertexArray(vaoIds[0])
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0])

        //将顶点属性与顶点缓冲对象关联起来
        GLES30.glVertexAttribPointer(
            positionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            5 * Float.SIZE_BYTES,
            0
        )
        GLES30.glVertexAttribPointer(
            texCoordHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            5 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        //解绑VAO
        GLES30.glBindVertexArray(0)

        val bitmap = context.loadBitmap(R.drawable.image)
        imageWidth = bitmap.width
        imageHeight = bitmap.height
        textureId = createTexture(bitmap)
        bitmap.recycle()
    }

    private var imageWidth = 0
    private var imageHeight = 0

    override fun sizeChange(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        val imageScale = imageWidth / imageHeight.toFloat()
        val windowScale = width / height.toFloat()
        val left: Float
        val right: Float
        val bottom: Float
        val top: Float
        if (imageScale > windowScale) {
            left = -1f
            right = 1f
            bottom = -imageScale / windowScale
            top = imageScale / windowScale
        } else if (imageScale < windowScale) {
            left = -windowScale / imageScale
            right = windowScale / imageScale
            bottom = -1f
            top = 1f
        } else {
            left = -1f
            right = 1f
            bottom = -1f
            top = 1f
        }

        //设置正交矩阵
        Matrix.orthoM(translateMatrix, 0, left, right, bottom, top, -1f, 1f)
    }

    override fun draw() {
        //使用Program
        GLES30.glUseProgram(program)

        //绑定VAO和EBO
        GLES30.glBindVertexArray(vaoIds[0])
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIds[0])

        //设置变换矩阵
        GLES30.glUniformMatrix4fv(matrixHandle, 1, false, translateMatrix, 0)

        //激活纹理单元
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glUniform1i(samplerHandle, 0)

        //使能顶点属性数组
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glEnableVertexAttribArray(texCoordHandle)

        //绘制三角形
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
        GLES30.glDrawElements(
            GLES30.GL_TRIANGLE_STRIP,
            6,
            GLES30.GL_UNSIGNED_INT,
            0 * Float.SIZE_BYTES
        )

        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(texCoordHandle)

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    override fun destroy() {
        GLES30.glDeleteBuffers(1, vboIds, 0)
        GLES30.glDeleteBuffers(1, eboIds, 0)
        GLES30.glDeleteVertexArrays(1, vaoIds, 0)
        deleteTexture(textureId)

        GLES30.glDeleteProgram(program)
    }
}