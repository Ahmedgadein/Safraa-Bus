package com.dinder.rihlabus.data.model

import com.dinder.rihlabus.utils.SeatUtils
import com.google.firebase.Timestamp
import java.util.*

data class Trip(
    val id: String? = null,
    val departure: Date,
    val company: Company? = null,
    val from: Destination? = null,
    val to: Destination? = null,
    val price: Int,
    val seats: List<Seat>
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id,
        "from" to from?.toJson(),
        "to" to to?.toJson(),
        "company" to company?.toJson(),
        "departure" to departure,
        "seats" to SeatUtils.seatsModelToMap(seats),
        "price" to price
    )

    companion object {
        fun fromJson(json: Map<String, Any>) = Trip(
            id = json["id"] as String? ?: "",
            departure = (json["departure"] as Timestamp).toDate(),
            company = Company.fromJson(json["company"] as Map<String, Any>),
            from = Destination.fromJson(json["from"] as Map<String, Any>),
            to = Destination.fromJson(json["to"] as Map<String, Any>),
            price = json["price"].toString().toInt(),
            seats = SeatUtils.seatsMapToModel(json["seats"] as Map<String, Map<String, Any?>>)
        )
    }
}
