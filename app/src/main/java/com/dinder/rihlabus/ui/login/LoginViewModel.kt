package com.dinder.rihlabus.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    init {
        viewModelScope.launch {
            repository.isLoggedIn().collect { loggedIn ->
                when (loggedIn) {
                    is Result.Loading -> {}
                    is Result.Error -> {}
                    is Result.Success -> {
                        _state.update { state ->
                            state.copy(isLoggedIn = loggedIn.value)
                        }
                    }
                }
            }
        }
    }

    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onNumberVerified(credential: AuthCredential, phoneNumber: String) {
        viewModelScope.launch {
            repository.isRegistered(phoneNumber).collect { registered ->
                when (registered) {
                    is Result.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showUserMessage(registered.message)
                    }
                    is Result.Success -> {
                        repository.login(credential, phoneNumber).collect { login ->
                            when (login) {
                                is Result.Loading -> {
                                    _state.update { it.copy(loading = true) }
                                }
                                is Result.Error -> {
                                    showUserMessage(login.message)
                                }
                                is Result.Success -> {
                                    _state.update {
                                        it.copy(
                                            loading = false,
                                            navigateToHome = login.value && registered.value,
                                            navigateToSignup = login.value && !registered.value
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onVerificationFailed() {
        hideLoading()
        showUserMessage("Login failed")
    }

    fun onSmsSent() {
        hideLoading()
    }

    fun onSendingSms() {
        showLoading()
    }

    private fun showLoading() {
        _state.update {
            it.copy(loading = true)
        }
    }

    private fun hideLoading() {
        _state.update {
            it.copy(loading = false)
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
