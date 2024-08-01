package com.pujh.opengl.camera.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.Matrix
import com.pujh.opengl.R
import com.pujh.opengl.camera.Filter
import com.pujh.opengl.util.createTexture
import com.pujh.opengl.util.deleteTexture
import com.pujh.opengl.util.loadAssets
import com.pujh.opengl.util.loadBitmap
import com.pujh.opengl.util.loadProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder

class JavaFilter(
    private val context: Context
) : Filter {

    private var vertices = floatArrayOf(
        //   ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
        0.9f, 0.9f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,  // 右上
        0.9f, -0.9f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,  // 右下
        -0.9f, -0.9f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,  // 左下
        -0.9f, 0.9f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f // 左上
    )

    private val indices = intArrayOf( // 注意索引从0开始!
        0, 1, 3,  // 第一个三角形
        1, 2, 3 // 第二个三角形
    )
    private val translateMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
//        Matrix.setRotateM(this, 0, 90f,1f, 0f, 0f)
    }

    private var program = 0

    private val vboIds = IntArray(1)
    private val eboIds = IntArray(1)
    private val vaoIds = IntArray(1)

    private var textureId = -1

    override fun init(oesTextureId: Int) {
        val vShaderSrc = context.loadAssets("filter/shader_base_v.glsl")
        val fShaderSrc = context.loadAssets("filter/shader_base_f.glsl")
        program = loadProgram(vShaderSrc, fShaderSrc)

        //分配内存空间,每个浮点型占4字节空间
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                //传入指定的坐标数据
                asFloatBuffer().put(vertices).position(0)
            }

        //创建VBO
        val vboIds = IntArray(1)
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

        //创建EBO
        val indexBuffer = ByteBuffer.allocateDirect(indices.size * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .apply {
                asIntBuffer().put(indices).position(0)
            }

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

        // Load the vertex data
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 8 * Float.SIZE_BYTES, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(
            1,
            3,
            GLES30.GL_FLOAT,
            false,
            8 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES
        )
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(
            2,
            2,
            GLES30.GL_FLOAT,
            false,
            8 * Float.SIZE_BYTES,
            6 * Float.SIZE_BYTES
        )
        GLES30.glEnableVertexAttribArray(2)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)

        //创建纹理
        val bitmap = context.loadBitmap(R.drawable.image)
        textureId = createTexture(bitmap)
        bitmap.recycle()

        val transformHandle = GLES30.glGetUniformLocation(program, "transform")
        val textureHandle = GLES30.glGetUniformLocation(program, "texture1")

        GLES30.glUseProgram(program)
        GLES30.glUniformMatrix4fv(transformHandle, 1, false, translateMatrix, 0)

        //激活纹理单元并绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        //一个纹理单元，可以绑定不同类型的纹理，在渲染时：
        //1、如果片元着色器中纹理采样器是 sampler2D 类型，则使用 GL_TEXTURE_2D 设置的纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        //1、如果片元着色器中纹理采样器是 samplerExternalOES 类型，则使用 GL_TEXTURE_EXTERNAL_OES 设置的纹理
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)

        //前面激活了哪个纹理单元，这里就应该给片元着色器设置对应序号
        GLES30.glUniform1i(textureHandle, 1)
    }

    override fun windowSizeChanged(width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun streamSizeChanged(width: Int, height: Int) {
    }

    override fun draw() {
        //使用Program
        GLES30.glUseProgram(program)

        //绑定VAO和EBO
        GLES30.glBindVertexArray(vaoIds[0])
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIds[0])

//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, vertices.size, GLES30.GL_UNSIGNED_INT, 0)

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