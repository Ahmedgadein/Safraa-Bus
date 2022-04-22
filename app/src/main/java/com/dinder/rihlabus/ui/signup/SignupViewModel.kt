package com.dinder.rihlabus.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.domain.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val useCase: RegisterUserUseCase) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    fun signup(
        user: User,
        company: String,
        location: String
    ) {
        viewModelScope.launch {
            useCase(user, company, location).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showUserMessage(result.message)
                    }
                    is Result.Success -> {
                        _state.update { state ->
                            state.copy(navigateToHome = result.value)
                        }
                    }
                }
            }
        }
    }

    private fun showUserMessage(content: String) {
        _state.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, content)
            it.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
