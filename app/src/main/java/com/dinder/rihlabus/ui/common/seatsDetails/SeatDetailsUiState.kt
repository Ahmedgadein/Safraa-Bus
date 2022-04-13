package com.dinder.rihlabus.ui.common.seatsDetails

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Seat

data class SeatDetailsUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val seats: List<Seat> = emptyList()
)
