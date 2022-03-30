package com.dinder.rihlabus.data.model

import com.google.firebase.Timestamp
import java.util.*

data class Trip(
    val date: Date,
    val time: Date,
    val company: String? = null,
    val from: String? = null,
    val to: String,
    val price: Int,
    val seats: Map<String, String>
) {
    fun toJson() = mapOf(
        "from" to from,
        "to" to to,
        "company" to company,
        "time" to time,
        "date" to date,
        "seats" to seats,
        "price" to price
    )

    companion object {
        fun fromJson(json: Map<String, Any>) = Trip(
            date = (json["date"] as Timestamp).toDate(),
            time = (json["time"] as Timestamp).toDate(),
            company = json["company"].toString(),
            from = json["from"].toString(),
            to = json["to"].toString(),
            price = json["price"].toString().toInt(),
            seats = json["seats"] as Map<String, String>
        )
    }
}
