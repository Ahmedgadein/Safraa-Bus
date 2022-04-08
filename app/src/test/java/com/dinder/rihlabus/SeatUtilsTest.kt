package com.dinder.rihlabus

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
        val expected: Map<String, Map<String, Any?>> = mapOf(
            "1" to mapOf(
                "status" to SeatState.UNBOOKED,
                "passenger" to null
            ),

            "2" to mapOf(
                "status" to SeatState.UNBOOKED,
                "passenger" to null
            )
        )

        assertEquals(expected, SeatUtils.getSelectedSeatsAsUnbooked(data))
    }

    @Test
    fun `get remote seats as local format should return true`() {
        val data: Map<String, Map<String, Any?>> = mapOf(
            "1" to mapOf(
                "status" to SeatState.BOOKED,
                "passenger" to null
            ),

            "2" to mapOf(
                "status" to SeatState.UNBOOKED,
                "passenger" to null
            )
        )

        val expected = mapOf(
            "1" to SeatState.BOOKED,
            "2" to SeatState.UNBOOKED
        )

        assertEquals(expected, SeatUtils.getRemoteSeats(data))
    }

    @Test
    fun `get booked seats count as string should return true`() {
        val data: Map<String, Map<String, Any?>> = mapOf(
            "1" to mapOf(
                "status" to SeatState.BOOKED,
                "passenger" to null
            ),

            "2" to mapOf(
                "status" to SeatState.UNBOOKED,
                "passenger" to null
            )
        )

        val expected = "1"

        assertEquals(expected, SeatUtils.getTripReservedSeatsCount(data))
    }

    @Test
    fun `get all seats count as string should return true`() {
        val data: Map<String, Map<String, Any?>> = mapOf(
            "1" to mapOf(
                "status" to SeatState.BOOKED,
                "passenger" to null
            ),

            "2" to mapOf(
                "status" to SeatState.UNBOOKED,
                "passenger" to null
            )
        )

        val expected = "2"

        assertEquals(expected, SeatUtils.getTripSeatsCount(data))
    }
}
