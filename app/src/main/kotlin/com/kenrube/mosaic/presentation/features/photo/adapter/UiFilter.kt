package com.kenrube.mosaic.presentation.features.photo.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kenrube.mosaic.domain.model.FilterType

data class UiFilter(
    val id: FilterType,
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int
)
