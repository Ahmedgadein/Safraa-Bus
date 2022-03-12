package com.dinder.rihlabus.utils

object PhoneNumberValidator {
    fun validate(mobile: String): String?{
        return when {
            !mobile.matches(Regex("[0-9]{9}")) -> {
                "Should be 9 numbers"
            }
            !mobile.matches(Regex("[1|9][0-9]{8}")) -> {
                "Invalid number"
            }
            else -> {
                null
            }
        }
    }
}