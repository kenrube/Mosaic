package com.kenrube.mosaic.opengl.filter

import android.opengl.GLES20

class SwirlFilter(vertexShader: String, fragmentShader: String) :
    ShaderFilter(vertexShader, fragmentShader) {

    private var angleLocation = 0

    private var angle: Float = 2f

    override fun onInit() {
        super.onInit()
        angleLocation = GLES20.glGetUniformLocation(program, "angle")
    }

    override fun onInitialized() {
        super.onInitialized()
        setAngle(angle)
    }

    override fun adjust(percentage: Int) {
        setAngle(range(percentage, 0f, 2f))
    }

    private fun setAngle(angle: Float) {
        this.angle = angle
        setFloat(angleLocation, angle)
    }
}