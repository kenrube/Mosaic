package com.kenrube.mosaic.presentation.features.photo_list

sealed class PhotoListEvent {
    object LoadPhotos : PhotoListEvent()
}