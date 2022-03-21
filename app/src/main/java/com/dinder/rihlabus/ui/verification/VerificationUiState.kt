package com.dinder.rihlabus.ui.verification

import com.dinder.rihlabus.common.Message

data class VerificationUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToSignup: Boolean = false,
    val messages: List<Message> = listOf()
)