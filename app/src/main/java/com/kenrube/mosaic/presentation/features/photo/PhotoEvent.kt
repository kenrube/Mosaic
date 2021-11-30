package com.kenrube.mosaic.presentation.features.photo

import android.graphics.Bitmap

sealed class PhotoEvent {
    class SavePhoto(val bitmap: Bitmap) : PhotoEvent()
    class SaveTempPhoto(val bitmap: Bitmap) : PhotoEvent()
}