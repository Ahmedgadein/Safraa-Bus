package com.dinder.rihlabus.common

sealed class Result<out Success, out Failure> {
    class Success<out Success>(val value: Success) : Result<Success, Nothing>()
    class Error<out Failure>(val reason: Failure) : Result<Nothing, Failure>()
}
