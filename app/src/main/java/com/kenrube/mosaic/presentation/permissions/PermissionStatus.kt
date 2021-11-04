package com.kenrube.mosaic.presentation.permissions

sealed class PermissionStatus {
    object PermissionGranted : PermissionStatus()
    object PermissionDenied : PermissionStatus()
    object Unknown : PermissionStatus()
}