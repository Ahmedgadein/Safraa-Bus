package com.dinder.rihlabus.utils

import com.dinder.rihlabus.common.Constants.NUMBER_OF_SEATS

enum class SeatState {
    UN_SELECTED,
    SELECTED,
    UNBOOKED,
    BOOKED
}

object SeatUtils {
    val emptySeats: Map<String, SeatState> = (1..NUMBER_OF_SEATS).map {
        Pair(
            it.toString(),
            SeatState.UN_SELECTED
        )
    }.toList().toMap()

    private fun getSelectedSeats(seats: Map<String, SeatState>): List<String> =
        seats.filter { it.value == SeatState.SELECTED }.keys.toList()

    fun getSelectedSeatsAsUnbooked(seats: Map<String, SeatState>): Map<String, Map<String, Any?>> {
        val selected = getSelectedSeats(seats)
        return selected.map {
            Pair(
                it,
                mapOf(
                    "status" to SeatState.UNBOOKED,
                    "passenger" to null
                )
            )
        }.toMap()
    }

    fun getRemoteSeats(seats: Map<String, Map<String, Any?>>): Map<String, SeatState> {
        return seats.map {
            Pair<String, SeatState>(
                it.key,
                SeatState.valueOf(value = it.value["status"].toString())
            )
        }.toMap()
    }

    fun getTripSeatsCount(seats: Map<String, Map<String, Any?>>) = seats.size.toString()

    fun getTripReservedSeatsCount(seats: Map<String, Map<String, Any>>) =
        seats.filter { SeatState.valueOf(it.value["status"].toString()) == SeatState.BOOKED }
            .size.toString()
}
