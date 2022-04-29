package com.dinder.rihlabus.data.model

import android.os.Parcelable
import com.dinder.rihlabus.utils.SeatState
import kotlinx.parcelize.Parcelize

@Parcelize
data class Seat(val number: Int, val passenger: String? = null, val status: SeatState) :
    Parcelable {

    val isAvailable: Boolean
        get() = status == SeatState.UNBOOKED && passenger == null

    fun toJson() = mapOf(
        "$number" to mapOf(
            "passenger" to passenger,
            "status" to status
        )
    )

    companion object {
        fun fromJson(json: Map<String, Map<String, Any?>>) =
            Seat(
                number = json.keys.first().toInt(),
                passenger = json.values.first()["passenger"] as String?,
                status = SeatState.valueOf(json.values.first()["status"].toString())
            )

        fun empty(): Seat = Seat(0, null, SeatState.UN_SELECTED)
    }
}
