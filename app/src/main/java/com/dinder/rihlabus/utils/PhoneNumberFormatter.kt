package com.dinder.rihlabus.utils

object PhoneNumberFormatter {
    fun getFullNumber(phoneNumber: String,countryCode: String = "+249"): String =
        countryCode + phoneNumber
}