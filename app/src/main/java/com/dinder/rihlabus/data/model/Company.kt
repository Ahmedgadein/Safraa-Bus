package com.dinder.rihlabus.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    val name: String,
    val arabicName: String? = null,
    val location: String
) : Parcelable{
    fun toJson(): Map<String, Any> = mapOf(
        "name" to name,
        "arabicName" to name,
        "location" to location
    )
}
