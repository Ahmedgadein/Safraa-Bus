package com.dinder.rihlabus.ui.newTrip

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Destination

data class NewTripUiState(
    val loading: Boolean = false,
    val isAdded: Boolean = false,
    val locations: List<Destination> = emptyList(),
    val selectedLocation: Destination? = null,
    val messages: List<Message> = emptyList()
)
