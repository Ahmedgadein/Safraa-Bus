package com.dinder.rihlabus.data.model

import java.util.*

data class Trip(
    val date: Date,
    val time: Date,
    val from: String,
    val to: String,
    val price: Int,
    val seats: Map<Int, Boolean>
) {
    fun toJson() = mapOf(
        "from" to from,
        "to" to to,
        "time" to time,
        "date" to date,
//        "seats" to seats,      TODO(Figure out how to serialize)
        "price" to price
    )
}
