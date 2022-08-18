package com.dinder.rihlabus.utils

import android.graphics.Color
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.Constants.NUMBER_OF_SEATS
import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.data.model.Trip

enum class SeatState {
    // Selection
    UN_SELECTED,
    SELECTED,

    // Reservation
    UNBOOKED,
    PRE_BOOK,
    PAYMENT_CONFIRMATION,
    PAID
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

    fun getSelectedSeatsAsUnbooked(seats: Map<String, SeatState>): List<Seat> {
        val selected = getSelectedSeats(seats)
        return selected.map {
            Seat(number = it.toInt(), passenger = null, status = SeatState.UNBOOKED)
        }
    }

    fun getTripSeatsCount(trip: Trip) = trip.seats.size.toString()

    fun getTripPaidSeatsCount(trip: Trip) =
        trip.seats.filter { it.status == SeatState.PAID }.size.toString()

    fun getTripAwaitingPaymentConfirmationCount(trip: Trip) =
        trip.seats.filter { it.status == SeatState.PAYMENT_CONFIRMATION }.size.toString()

    fun getTripPreBookingCount(trip: Trip) =
        trip.seats.filter { it.status == SeatState.PRE_BOOK }.size.toString()

    // Convert List of [Seat] to SeatView state Map
    fun getSeatsListAsStateMap(seats: List<Seat>): Map<String, SeatState> = seats.associate {
        it.number.toString() to it.status
    }

    // Convert remote json Map to list of [Seat]
    fun seatsMapToModel(seats: Map<String, Map<String, Any?>>): List<Seat> = seats.map {
        Seat.fromJson(
            mapOf(
                it.key to mapOf(
                    "passenger" to it.value["passenger"] as String?,
                    "status" to SeatState.valueOf(it.value["status"].toString()),
                    "passengerPhoneNumber" to it.value["passengerPhoneNumber"] as String?,
                    "paidAmount" to it.value["paidAmount"]
                )
            )
        )
    }.toList().sortedBy { it.number }

    // Convert list of [Seat] to map
    fun seatsModelToMap(seats: List<Seat>): Map<String, Map<String, Any?>> = seats.associate {
        it.number.toString() to mapOf(
            "passenger" to it.passenger,
            "status" to it.status
        )
    }

    fun getSeatStateColor(seat: Seat): Int {
        return when (seat.status) {
            SeatState.PAID -> R.color.green
            SeatState.PAYMENT_CONFIRMATION -> R.color.orange
            SeatState.PRE_BOOK -> R.color.teal_200
            else -> Color.GRAY
        }
    }
}
