package com.dinder.rihlabus.ui.home

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Trip

data class TripsUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val trips: List<Trip> = emptyList()
)
