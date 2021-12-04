package com.kenrube.mosaic.opengl.filter

import android.opengl.GLES20

class SolarizeFilter(vertexShader: String, fragmentShader: String) :
    ShaderFilter(vertexShader, fragmentShader) {

    private var uniformThresholdLocation = 0

    private var threshold: Float = 1f

    override fun onInit() {
        super.onInit()
        uniformThresholdLocation = GLES20.glGetUniformLocation(program, "threshold")
    }

    override fun onInitialized() {
        super.onInitialized()
        setThreshold(threshold)
    }

    override fun adjust(percentage: Int) {
        setThreshold(range(percentage, 0f, 1f))
    }

    private fun setThreshold(threshold: Float) {
        this.threshold = threshold
        setFloat(uniformThresholdLocation, threshold)
    }
}