package com.kenrube.mosaic.data.resource

import com.kenrube.mosaic.domain.model.Shader
import com.kenrube.mosaic.domain.model.FilterType

interface ShaderRepository {
    fun getShader(id: FilterType): Shader
}