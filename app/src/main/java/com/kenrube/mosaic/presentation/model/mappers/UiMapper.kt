package com.kenrube.mosaic.presentation.model.mappers

interface UiMapper<in E, out V> {
    fun mapToView(input: E): V
}