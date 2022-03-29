package com.dinder.rihlabus.common

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    class Success<out T>(val value: T) : Result<T>()
    class Error(val message: String) : Result<Nothing>()
}
