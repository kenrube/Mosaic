package com.kenrube.mosaic.domain.usecases

import com.kenrube.mosaic.data.db.PhotoRepository
import javax.inject.Inject

class GetPhotos @Inject constructor(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke() = photoRepository.getPhotos()
}