package com.kenrube.mosaic.presentation.features.photo

import android.net.Uri
import com.kenrube.mosaic.presentation.Event

data class PhotoViewState(
    val photoStored: Event<Uri>? = null,
    val photoNotStored: Event<Unit>? = null,
    val tempPhotoStored: Event<Uri>? = null,
    val tempPhotoNotStored: Event<Unit>? = null
)
