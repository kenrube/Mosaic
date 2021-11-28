package com.kenrube.mosaic.opengl

import android.opengl.GLES20
import java.nio.FloatBuffer

open class ShaderFilter(
    private val vertexShader: String,
    private val fragmentShader: String
) {
    var program = -1
    private var attribPosition = -1
    private var uniformTexture = -1
    private var attribTextureCoordinate = -1
    private var isInitialized = false

    fun initIfNecessary() {
        if (!isInitialized) {
            onInit()
            onInitialized()
        }
    }

    open fun onInit() {
        program = loadProgram(vertexShader, fragmentShader)
        attribPosition = GLES20.glGetAttribLocation(program, "position")
        uniformTexture = GLES20.glGetUniformLocation(program, "inputImageTexture")
        attribTextureCoordinate = GLES20.glGetAttribLocation(program, "inputTextureCoordinate")
        isInitialized = true
    }

    open fun onInitialized() {
        // override if necessary
    }

    fun onDraw(textureId: Int, cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        GLES20.glUseProgram(program)
        if (!isInitialized) {
            return
        }
        cubeBuffer.position(0)
        GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer)
        GLES20.glEnableVertexAttribArray(attribPosition)
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(
            attribTextureCoordinate,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureBuffer
        )
        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
        if (textureId != NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uniformTexture, 0)
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(attribPosition)
        GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    open fun onOutputSizeChanged(width: Int, height: Int) {
        // override if necessary
    }

    fun destroy() {
        isInitialized = false
        GLES20.glDeleteProgram(program)
        onDestroy()
    }

    open fun onDestroy() {
        // override if necessary
    }
}