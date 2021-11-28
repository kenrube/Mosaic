package com.kenrube.mosaic.opengl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.roundToInt

class ShaderRenderer(private var filter: ShaderFilter) : GLSurfaceView.Renderer {

    private var glTextureId = NO_TEXTURE
    private val glCubeBuffer = ByteBuffer.allocateDirect(CUBE.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().also {
            it.put(CUBE).position(0)
        }
    private val glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().also {
            it.put(TEXTURE).position(0)
        }

    private var imageWidth = -1
    private var imageHeight = -1

    var frameWidth: Int = -1
        private set
    var frameHeight: Int = -1
        private set

    init {
        adjustImageScaling()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        filter.initIfNecessary()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        frameWidth = width
        frameHeight = height
        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(filter.program)
        filter.onOutputSizeChanged(width, height)
        adjustImageScaling()
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        filter.onDraw(glTextureId, glCubeBuffer, glTextureBuffer)
    }

    fun setFilter(filter: ShaderFilter) {
        val oldFilter = filter
        this.filter = filter
        oldFilter.destroy()
        this.filter.initIfNecessary()
        GLES20.glUseProgram(this.filter.program)
        this.filter.onOutputSizeChanged(frameWidth, frameHeight)
    }

    fun setImageBitmap(bitmap: Bitmap, recycle: Boolean = true) {
        var resizedBitmap: Bitmap? = null
        if (bitmap.width % 2 == 1) {
            resizedBitmap = Bitmap.createBitmap(
                bitmap.width + 1, bitmap.height,
                Bitmap.Config.ARGB_8888
            )
            resizedBitmap.density = bitmap.density
            val can = Canvas(resizedBitmap)
            can.drawARGB(0x00, 0x00, 0x00, 0x00)
            can.drawBitmap(bitmap, 0f, 0f, null)
        }
        glTextureId = loadTexture(resizedBitmap ?: bitmap, glTextureId, recycle)
        resizedBitmap?.recycle()
        imageWidth = bitmap.width
        imageHeight = bitmap.height
        adjustImageScaling()
    }

    fun deleteImage() {
        GLES20.glDeleteTextures(1, intArrayOf(glTextureId), 0)
        glTextureId = NO_TEXTURE
    }

    private fun adjustImageScaling() {
        val outputWidth = frameWidth.toFloat()
        val outputHeight = frameHeight.toFloat()
        val ratio1 = outputWidth / imageWidth
        val ratio2 = outputHeight / imageHeight
        val ratioMax = max(ratio1, ratio2)
        val imageWidthNew = (imageWidth * ratioMax).roundToInt()
        val imageHeightNew = (imageHeight * ratioMax).roundToInt()
        val ratioWidth = imageWidthNew / outputWidth
        val ratioHeight = imageHeightNew / outputHeight
        val cube: FloatArray = floatArrayOf(
            CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
            CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
            CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
            CUBE[6] / ratioHeight, CUBE[7] / ratioWidth
        )
        glCubeBuffer.clear()
        glCubeBuffer.put(cube).position(0)
        glTextureBuffer.clear()
        glTextureBuffer.put(TEXTURE).position(0)
    }

    companion object {
        private val CUBE = floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )
        private val TEXTURE = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
        )
    }
}