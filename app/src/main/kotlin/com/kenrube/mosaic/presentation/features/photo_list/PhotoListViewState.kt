package com.kenrube.mosaic.presentation.features.photo_list

import com.kenrube.mosaic.presentation.model.UiPhoto

data class PhotoListViewState(
    val loading: Boolean = true,
    val showingPermissionWarning: Boolean = false,
    val photos: List<UiPhoto> = emptyList()
)