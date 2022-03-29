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
                        _loginUiState.update { state ->
                            state.copy(isLoggedIn = loggedIn.value)
                        }
                    }
                }
            }
        }
    }

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun onNumberVerified(credential: AuthCredential, phoneNumber: String) {
        viewModelScope.launch {
            repository.isRegistered(phoneNumber).collect { registered ->
                when (registered) {
                    is Result.Loading -> {
                        _loginUiState.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showUserMessage(registered.message)
                    }
                    is Result.Success -> {
                        repository.login(credential, phoneNumber).collect { login ->
                            when (login) {
                                is Result.Loading -> {
                                    _loginUiState.update { it.copy(loading = true) }
                                }
                                is Result.Error -> {
                                    showUserMessage(login.message)
                                }
                                is Result.Success -> {
                                    _loginUiState.update {
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
        _loginUiState.update {
            it.copy(loading = true)
        }
    }

    private fun hideLoading() {
        _loginUiState.update {
            it.copy(loading = false)
        }
    }

    private fun showUserMessage(content: String) {
        _loginUiState.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, content)
            it.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _loginUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}
