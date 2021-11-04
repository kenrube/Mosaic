package com.kenrube.mosaic.data.db

import com.kenrube.mosaic.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>
}