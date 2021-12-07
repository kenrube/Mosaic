package com.kenrube.mosaic.domain.model

data class Shader(
    val vertexShader: String,
    val fragmentShader: String
)

enum class FilterType(val defaultIntensity: Int) {
    NONE(-1),
    SATURATION(0 /* bw */),
    INVERT(100 /* negative */),
    PIXELATION(15),
    SWIRL(100)
}
