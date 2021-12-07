package com.kenrube.mosaic.opengl

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

const val NO_TEXTURE = -1

private fun loadShader(source: String, type: Int): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, source)
    GLES20.glCompileShader(shader)
    return shader
}

fun loadProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
    val vertexShader = loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER)
    val fragmentShader = loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER)
    val program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program, vertexShader)
    GLES20.glAttachShader(program, fragmentShader)
    GLES20.glLinkProgram(program)
    return program
}

fun loadTexture(
    img: Bitmap,
    usedTextureId: Int,
    recycle: Boolean = true
): Int {
    val textures = IntArray(1)
    if (usedTextureId == NO_TEXTURE) {
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0)
    } else {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTextureId)
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img)
        textures[0] = usedTextureId
    }
    if (recycle) {
        img.recycle()
    }
    return textures[0]
}