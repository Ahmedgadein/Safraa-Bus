package com.dinder.rihlabus.ui.tripDetails

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Trip

data class TripDetailUiState(
    val loading: Boolean = false,
    val confirmPaymentLoading: Boolean = false,
    val bookSeatLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val trip: Trip? = null
)
