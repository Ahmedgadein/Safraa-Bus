package com.dinder.rihlabus.data.model

import android.os.Parcelable
import com.dinder.rihlabus.utils.SeatState
import kotlinx.parcelize.Parcelize

@Parcelize
data class Seat(
    val number: Int,
    val passenger: String? = null,
    val passengerPhoneNumber: String? = null,
    val paidAmount: Int? = null,
    val status: SeatState
) :
    Parcelable {

    val isAvailable: Boolean
        get() = status == SeatState.UNBOOKED && passenger == null

    fun toJson() = mapOf(
        "$number" to mapOf(
            "passenger" to passenger,
            "passengerPhoneNumber" to passengerPhoneNumber,
            "status" to status
        )
    )

    companion object {
        fun fromJson(json: Map<String, Map<String, Any?>>) =
            Seat(
                number = json.keys.first().toInt(),
                passenger = json.values.first()["passenger"] as String?,
                passengerPhoneNumber = json.values.first()["passengerPhoneNumber"] as String?,
                status = SeatState.valueOf(json.values.first()["status"].toString()),
                paidAmount = json.values.first()["paidAmount"].toString().toIntOrNull() ?: 0
            )
    }
}
