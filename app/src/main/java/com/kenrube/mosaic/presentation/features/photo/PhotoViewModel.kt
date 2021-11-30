package com.kenrube.mosaic.presentation.features.photo

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenrube.mosaic.data.db.PhotoRepository
import com.kenrube.mosaic.presentation.Event
import com.kenrube.mosaic.utils.coroutine.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoViewState())
    val state = _state.asStateFlow()

    fun onEvent(event: PhotoEvent) {
        when (event) {
            is PhotoEvent.SavePhoto -> savePhoto(event.bitmap)
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        viewModelScope.launch(dispatchersProvider.io()) {
            try {
                val photoUri = photoRepository.savePhoto(bitmap)
                _state.value = state.value.copy(
                    photoStored = Event(photoUri)
                )
            } catch (e: IOException) {
                _state.value = state.value.copy(
                    photoNotStored = Event(Unit)
                )
            }
        }
    }
}