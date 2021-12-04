package com.kenrube.mosaic.data.db

import android.graphics.Bitmap
import android.net.Uri
import com.kenrube.mosaic.domain.model.Photo

interface PhotoRepository {
    suspend fun getPhotos(): List<Photo>

    suspend fun savePhoto(bitmap: Bitmap): Uri

    suspend fun savePhotoToCache(bitmap: Bitmap): Uri
}