package com.dinder.rihlabus

import com.dinder.rihlabus.utils.DateTimeUtils
import java.util.*
import junit.framework.Assert.assertEquals
import org.junit.Test

class DateTimeUtilsTest {
    @Test
    fun getTimeInstanceTest() {
        val time = "19:00"
        val timeInstance = Calendar.getInstance()
        val testCase = DateTimeUtils.getTimeInstance(time)
        timeInstance.set(Calendar.HOUR_OF_DAY, 19)
        timeInstance.set(Calendar.MINUTE, 0)

        assertEquals(timeInstance.time.hours, testCase.hours)
        assertEquals(timeInstance.time.minutes, testCase.minutes)
    }

    @Test
    fun getTimeDateTest() {
        val date = "1/1/2001"
        val dateInstance = Calendar.getInstance()
        val testCase = DateTimeUtils.getDateInstance(date)
        dateInstance.set(Calendar.DAY_OF_MONTH, 1)
        dateInstance.set(Calendar.MONTH, 1)
        dateInstance.set(Calendar.YEAR, 2001)

        assertEquals(dateInstance.time.day, testCase.day)
        assertEquals(dateInstance.time.month, testCase.month)
        assertEquals(dateInstance.time.year, testCase.year)
    }
}
