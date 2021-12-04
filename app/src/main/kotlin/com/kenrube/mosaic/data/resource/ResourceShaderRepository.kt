package com.kenrube.mosaic.data.resource

import android.content.Context
import com.kenrube.mosaic.R
import com.kenrube.mosaic.domain.model.Shader
import com.kenrube.mosaic.domain.model.FilterType
import com.kenrube.mosaic.utils.getRawFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceShaderRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : ShaderRepository {
    private val shaderMap = hashMapOf<FilterType, Shader>()

    init {
        shaderMap[FilterType.NONE] = Shader(
            context.getRawFile(R.raw.vertex_default),
            context.getRawFile(R.raw.fragment_default)
        )
        shaderMap[FilterType.PIXELATION] = Shader(
            context.getRawFile(R.raw.vertex_default),
            context.getRawFile(R.raw.fragment_pixelation)
        )
        shaderMap[FilterType.SATURATION] = Shader(
            context.getRawFile(R.raw.vertex_default),
            context.getRawFile(R.raw.fragment_saturation)
        )
        shaderMap[FilterType.SOLARIZE] = Shader(
            context.getRawFile(R.raw.vertex_default),
            context.getRawFile(R.raw.fragment_solarize)
        )
        shaderMap[FilterType.SWIRL] = Shader(
            context.getRawFile(R.raw.vertex_default),
            context.getRawFile(R.raw.fragment_swirl)
        )
    }

    override fun getShader(id: FilterType): Shader = shaderMap[id]
        ?: throw IllegalArgumentException("Shader for filter $id is not instantiated")
}