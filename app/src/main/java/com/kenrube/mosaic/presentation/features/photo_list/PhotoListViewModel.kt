package com.kenrube.mosaic.presentation.features.photo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenrube.mosaic.domain.usecases.GetPhotos
import com.kenrube.mosaic.presentation.model.mappers.UiPhotoMapper
import com.kenrube.mosaic.presentation.permissions.PermissionStatus
import com.kenrube.mosaic.utils.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val getPhotos: GetPhotos,
    private val uiPhotoMapper: UiPhotoMapper,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoListViewState())
    val state = _state.asStateFlow()

    var storagePermissionStatus: PermissionStatus
            by Delegates.observable(PermissionStatus.Unknown) { _, oldValue, newValue ->
                if (newValue != oldValue) {
                    when (newValue) {
                        is PermissionStatus.PermissionGranted -> {
                            onEvent(PhotoListEvent.LoadPhotos)
                        }
                        else -> { // PermissionStatus.PermissionDenied
                            _state.value = state.value.copy(
                                loading = false,
                                showingPermissionWarning = true,
                                photos = emptyList()
                            )
                        }
                    }
                }
            }

    private fun onEvent(event: PhotoListEvent) {
        when (event) {
            is PhotoListEvent.LoadPhotos -> loadPhotos()
        }
    }

    private fun loadPhotos() {
        viewModelScope.launch(dispatchersProvider.io()) {
            val photos = getPhotos().map { uiPhotoMapper.mapToView(it) }

            _state.value = state.value.copy(
                loading = false,
                showingPermissionWarning = false,
                photos = photos
            )
        }
    }
}