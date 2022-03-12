package com.dinder.rihlabus.ui.verification

import com.dinder.rihlabus.common.Message

data class VerificationUiState(
    val loading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val messages: List<Message> = listOf()
)