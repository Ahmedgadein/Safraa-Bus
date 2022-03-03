package com.dinder.rihlabus.common

sealed class Response<out T>(
    private val data: T? = null,
    private val message: String? = null
) {
    class Success<T>(data: T) : Response<T>(data = data)
    class Error(message: String) : Response<Nothing>(message = message)
    class Loading : Response<Nothing>()
}
