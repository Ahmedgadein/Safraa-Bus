package com.dinder.rihlabus.ui.login

import com.dinder.rihlabus.common.Message

data class LoginUiState(
    val loading: Boolean = false,
    val isRegistered: Boolean = false,
    val isLoggedIn: Boolean = false,
    val messages: List<Message> = listOf()
)