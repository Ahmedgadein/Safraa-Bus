package com.dinder.rihlabus.ui.home.currentTrips

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Trip

data class CurrentTripsUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val trips: List<Trip> = emptyList()
)
