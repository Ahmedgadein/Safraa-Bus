package com.dinder.rihlabus.utils

object SeatUtils {
    fun getSelectedSeats(seats: Map<Int, Boolean>): List<Int> =
        seats.filter { it.value }.keys.toList()

    fun getSelectedSeatsAsUnbooked(seats: Map<Int, Boolean>): Map<String, String> {
        val selected = getSelectedSeats(seats)
        return selected.map {
            Pair(it.toString(), "unbooked")
        }.toMap()
    }
}