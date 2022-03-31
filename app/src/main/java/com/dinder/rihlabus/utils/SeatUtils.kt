package com.dinder.rihlabus.utils

object SeatUtils {
    fun getSelectedSeats(seats: Map<Int, Boolean>): List<Int> =
        seats.filter { it.value }.keys.toList()

    fun getSelectedSeatsAsUnbooked(seats: Map<Int, Boolean>): Map<String, Map<String, Any?>> {
        val selected = getSelectedSeats(seats)
        return selected.map {
            Pair(
                it.toString(),
                mapOf(
                    "status" to "unbooked",
                    "passenger" to null
                )
            )
        }.toMap()
    }

    fun getTripSeatsCount(seats: Map<String, Map<String, Any?>>) = seats.size.toString()

    fun getTripReservedSeatsCount(seats: Map<String, Map<String, Any>>) =
        seats.filter { it.value["status"] == "reserved" }.size.toString()
}
