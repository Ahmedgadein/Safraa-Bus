package com.dinder.rihlabus.ui.newTrip

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination

data class NewTripUiState(
    val loading: Boolean = false,
    val isAdded: Boolean = false,
    val locations: List<Destination> = emptyList(),
    val companies: List<Company> = emptyList(),
    val from: Destination? = null,
    val to: Destination? = null,
    val company: Company? = null,
    val messages: List<Message> = emptyList()
)
