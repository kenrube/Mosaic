package com.kenrube.mosaic.opengl.filter

import android.opengl.GLES20

class PixelationFilter(vertexShader: String, fragmentShader: String) :
    ShaderFilter(vertexShader, fragmentShader) {

    private var imageWidthFactorLocation = 0
    private var imageHeightFactorLocation = 0
    private var pixelLocation = 0

    private var pixel = 100f

    override fun onInit() {
        super.onInit()
        imageWidthFactorLocation = GLES20.glGetUniformLocation(program, "imageWidthFactor")
        imageHeightFactorLocation = GLES20.glGetUniformLocation(program, "imageHeightFactor")
        pixelLocation = GLES20.glGetUniformLocation(program, "pixel")
    }

    override fun onInitialized() {
        super.onInitialized()
        setPixel(pixel)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setFloat(imageWidthFactorLocation, 1f / width)
        setFloat(imageHeightFactorLocation, 1f / height)
    }

    override fun adjust(percentage: Int) {
        setPixel(range(percentage, 1f, 100f))
    }

    private fun setPixel(pixel: Float) {
        this.pixel = pixel
        setFloat(pixelLocation, this.pixel)
    }
}