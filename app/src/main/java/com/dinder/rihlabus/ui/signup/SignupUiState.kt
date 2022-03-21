package com.dinder.rihlabus.ui.signup

import com.dinder.rihlabus.common.Message

data class SignupUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val messages: List<Message> = listOf()
)