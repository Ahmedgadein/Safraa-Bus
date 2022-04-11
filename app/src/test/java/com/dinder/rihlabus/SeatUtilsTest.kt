package com.dinder.rihlabus

import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.utils.SeatState
import com.dinder.rihlabus.utils.SeatUtils
import junit.framework.Assert.assertEquals
import org.junit.Test

class SeatUtilsTest {

    @Test
    fun `get selected seats as UNBOOKED should return true`() {
        val data = mapOf(
            "1" to SeatState.SELECTED,
            "2" to SeatState.SELECTED
        )
        val expected = listOf(
            Seat(number = 1, status = SeatState.UNBOOKED),
            Seat(number = 2, status = SeatState.UNBOOKED)
        )

        assertEquals(expected, SeatUtils.getSelectedSeatsAsUnbooked(data))
    }

    @Test
    fun `get remote seats as local format should return true`() {
        val data = listOf(
            Seat(number = 1, status = SeatState.BOOKED),
            Seat(number = 2, status = SeatState.UNBOOKED)
        )

        val expected = mapOf(
            "1" to mapOf(
                "passenger" to null,
                "status" to SeatState.BOOKED
            ),
            "2" to mapOf(
                "passenger" to null,
                "status" to SeatState.UNBOOKED
            )
        )

        assertEquals(expected, SeatUtils.seatsModelToMap(data))
    }

    @Test
    fun `get booked seats count as string should return true`() {
        val data = listOf(
            Seat(number = 1, status = SeatState.BOOKED),
            Seat(number = 2, status = SeatState.UNBOOKED)
        )

        val expected = "1"

        assertEquals(expected, SeatUtils.getTripReservedSeatsCount(data))
    }

    @Test
    fun `get all seats count as string should return true`() {
        val data = listOf(
            Seat(number = 1, status = SeatState.BOOKED),
            Seat(number = 2, status = SeatState.UNBOOKED)
        )

        val expected = "2"

        assertEquals(expected, SeatUtils.getTripSeatsCount(data))
    }
}
