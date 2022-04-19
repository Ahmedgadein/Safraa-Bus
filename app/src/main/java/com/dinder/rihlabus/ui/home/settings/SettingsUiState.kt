package com.dinder.rihlabus.ui.home.settings

import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.User

data class SettingsUiState(
    val loading: Boolean = false,
    val user: User? = null,
    val messages: List<Message> = emptyList()
)
