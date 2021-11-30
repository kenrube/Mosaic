package com.kenrube.mosaic.presentation

import kotlin.random.Random

data class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    // ViewModel's MutableStateFlow won't emit value if it equals to previous. But, in practice,
    // we can receive the same result from repository, which then won't be received in view layer.
    // Ugly hack, yeah.

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = Random.nextInt()
}
