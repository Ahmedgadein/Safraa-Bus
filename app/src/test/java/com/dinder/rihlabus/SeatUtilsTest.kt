package com.dinder.rihlabus

import com.dinder.rihlabus.utils.SeatUtils
import junit.framework.Assert.assertEquals
import org.junit.Test

class SeatUtilsTest {

    @Test
    fun getSelectedSeatsAsUnbookedTest() {
        val data = mapOf(
            1 to true,
            2 to true,
            3 to true,
            4 to false,
            5 to false,
            6 to false
        )
        val expected = mapOf(
            "1" to "unbooked",
            "2" to "unbooked",
            "3" to "unbooked"
        )

        assertEquals(expected, SeatUtils.getSelectedSeatsAsUnbooked(data))
    }
}