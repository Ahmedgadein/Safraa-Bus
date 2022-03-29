package com.dinder.rihlabus.ui.home.newTrip

import com.dinder.rihlabus.common.Message

data class NewTripUiState(
    val loading: Boolean = false,
    val isAdded: Boolean = false,
    val messages: List<Message> = emptyList()
)
