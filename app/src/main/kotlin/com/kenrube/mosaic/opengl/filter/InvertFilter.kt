package com.kenrube.mosaic.opengl.filter

import android.opengl.GLES20

class InvertFilter(vertexShader: String, fragmentShader: String) :
    ShaderFilter(vertexShader, fragmentShader) {

    private var uniformSubtrahendLocation = 0

    private var subtrahend: Float = 1f

    override fun onInit() {
        super.onInit()
        uniformSubtrahendLocation = GLES20.glGetUniformLocation(program, "subtrahend")
    }

    override fun onInitialized() {
        super.onInitialized()
        setSubtrahend(subtrahend)
    }

    override fun adjust(percentage: Int) {
        setSubtrahend(range(percentage, 0f, 1f))
    }

    private fun setSubtrahend(subtrahend: Float) {
        this.subtrahend = subtrahend
        setFloat(uniformSubtrahendLocation, subtrahend)
    }
}