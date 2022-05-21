package com.dinder.rihlabus.ui.signup

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination

data class SignupUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val locations: List<Destination> = emptyList(),
    val companies: List<Company> = emptyList(),
    val selectedCompany: Company? = null,
    val selectedLocation: Destination? = null,
    val messages: List<Message> = listOf()
)
