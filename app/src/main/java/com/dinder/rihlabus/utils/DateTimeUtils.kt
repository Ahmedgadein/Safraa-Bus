package com.dinder.rihlabus.utils

import java.util.*

object DateTimeUtils {
    fun getTimeInstance(formattedTime: String): Date {
        val timeInputs = formattedTime.split(":").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, timeInputs[0])
        date.set(Calendar.MINUTE, timeInputs[1])
        return date.time
    }

    fun getDateInstance(formattedTime: String): Date {
        val timeInputs = formattedTime.split("/").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(timeInputs[2], timeInputs[1], timeInputs[0])
        return date.time
    }

    fun tripDeparture(date: String, time: String): Date {
        val date = getDateInstance(date)
        val time = getTimeInstance(time)

        val cal = Calendar.getInstance()
        cal.time = time
        val hour = cal[Calendar.HOUR_OF_DAY]
        val min = cal[Calendar.MINUTE]
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = min
        return cal.time
    }

    fun getFormattedDate(date: Date): String {
        val calendar = GregorianCalendar.getInstance().apply {
            this.time = date
        }

        return String.format(
            "%d-%d-%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(GregorianCalendar.MONTH) + 1, // Calendar.MONTH is month order
            calendar.get(Calendar.YEAR)
        )
    }

    fun getFormattedTime(date: Date): String {
        val calendar = Calendar.getInstance().apply {
            this.time = date
        }

        return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
            String.format("%02d", calendar.get(Calendar.MINUTE))
    }
}
