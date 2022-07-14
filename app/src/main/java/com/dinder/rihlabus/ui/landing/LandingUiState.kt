package com.dinder.rihlabus.ui.landing

import com.dinder.rihlabus.common.Message

data class LandingUiState(
    val messages: List<Message> = emptyList(),
    val navigateToHome: Boolean = false,
    val navigateToLogin: Boolean = false,
    val navigateToUpdateScreen: Boolean = false
)
