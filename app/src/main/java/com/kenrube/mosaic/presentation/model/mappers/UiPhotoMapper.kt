package com.kenrube.mosaic.presentation.model.mappers

import com.kenrube.mosaic.domain.model.Photo
import com.kenrube.mosaic.presentation.model.UiPhoto
import javax.inject.Inject

class UiPhotoMapper @Inject constructor() : UiMapper<Photo, UiPhoto> {
    override fun mapToView(input: Photo) =
        UiPhoto(
            uri = input.uri.ifEmpty { null }
        )
}