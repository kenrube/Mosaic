package com.kenrube.mosaic.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController

fun <T> Fragment.getNavigationResultLiveData(key: String): MutableLiveData<T>? =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData(key)

fun <T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}
