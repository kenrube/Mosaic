package com.kenrube.mosaic.opengl.filter

interface Adjuster {
    fun adjust(percentage: Int)

    fun range(percentage: Int, start: Float, end: Float): Float =
        (end - start) * percentage / 100f + start
}