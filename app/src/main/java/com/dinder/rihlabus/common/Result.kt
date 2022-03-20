package com.dinder.rihlabus.common

sealed class Result<out Success> {
    object Loading: Result<Nothing>()
    class Success<out Success>(val value: Success) : Result<Success>()
    class Error(val message: String) : Result<Nothing>()
}
