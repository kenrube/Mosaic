package com.kenrube.mosaic.opengl.filter

import android.opengl.GLES20

class SaturationFilter(vertexShader: String, fragmentShader: String) :
    ShaderFilter(vertexShader, fragmentShader) {

    private var saturationLocation = 0

    private var saturation: Float = 2f

    override fun onInit() {
        super.onInit()
        saturationLocation = GLES20.glGetUniformLocation(program, "saturation")
    }

    override fun onInitialized() {
        super.onInitialized()
        setSaturation(saturation)
    }

    override fun adjust(percentage: Int) {
        setSaturation(range(percentage, 0f, 2f))
    }

    private fun setSaturation(saturation: Float) {
        this.saturation = saturation
        setFloat(saturationLocation, this.saturation)
    }
}