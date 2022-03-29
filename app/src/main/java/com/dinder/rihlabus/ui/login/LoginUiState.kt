package com.dinder.rihlabus.ui.login

import com.dinder.rihlabus.common.Message

data class LoginUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToSignup: Boolean = false,
    val isLoggedIn: Boolean = false,
    val messages: List<Message> = listOf()
)
