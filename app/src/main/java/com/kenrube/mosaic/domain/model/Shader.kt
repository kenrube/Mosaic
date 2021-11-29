package com.kenrube.mosaic.domain.model

data class Shader(
    val vertexShader: String,
    val fragmentShader: String
)

enum class FilterType {
    NONE,
    PIXELATION,
    SATURATION,
    SOLARIZE,
    SWIRL
}
