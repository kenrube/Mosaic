package com.kenrube.mosaic.presentation.features.photo_list.adapter

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class UiModel(open val id: Int) {

    data class PhotoUiModel(
        override val id: Int,
        val uri: Uri
    ) : UiModel(id)

    data class ActionUiModel(
        override val id: Int,
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        val action: String
    ) : UiModel(id)
}