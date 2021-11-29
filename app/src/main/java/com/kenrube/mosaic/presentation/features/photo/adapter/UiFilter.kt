package com.kenrube.mosaic.presentation.features.photo.adapter

import android.net.Uri
import com.kenrube.mosaic.domain.model.FilterType

data class UiFilter(
    val id: FilterType,
    val image: Uri,
    val title: String
)
