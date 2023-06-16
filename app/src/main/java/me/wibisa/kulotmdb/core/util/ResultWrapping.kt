package me.wibisa.kulotmdb.core.util

sealed class ResultWrapping<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultWrapping<T>()
    data class Error(val message: String) : ResultWrapping<Nothing>()
    object Loading : ResultWrapping<Nothing>()
    object Empty : ResultWrapping<Nothing>()

    val extractData: T?
        get() = when (this) {
            is Success -> data
            is Error -> null
            is Loading -> null
            is Empty -> null
        }
}