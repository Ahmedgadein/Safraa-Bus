package com.dinder.rihlabus.utils

import java.util.*

object DateTimeUtils {
    fun getTimeInstance(formattedTime: String): Date{
        val timeInputs = formattedTime.split(":").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, timeInputs[0])
        date.set(Calendar.MINUTE, timeInputs[1])
        return  date.time
    }

    fun getDateInstance(formattedTime: String): Date{
        val timeInputs = formattedTime.split("/").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(timeInputs[2],timeInputs[1], timeInputs[0])
        return  date.time
    }
}