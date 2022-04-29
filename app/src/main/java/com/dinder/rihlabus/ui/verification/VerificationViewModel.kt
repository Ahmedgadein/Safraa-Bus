package com.dinder.rihlabus.ui.verification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.remote.repository.auth.AuthRepository
import com.dinder.rihlabus.data.remote.repository.user.UserRepository
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
class VerificationViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow(VerificationUiState())
    val state = _state.asStateFlow()

    fun onVerificationAttempt(credential: AuthCredential, phoneNumber: String) {
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
                        Log.i("LoginFlow", "isRegistered Value: ${registered.value}")
                        userRepository.user.collect { user ->
                            repository.login(credential, phoneNumber).collect { login ->
                                when (login) {
                                    is Result.Loading -> {
                                        _state.update { it.copy(loading = true) }
                                    }
                                    is Result.Error -> {
                                        showUserMessage(login.message)
                                    }
                                    is Result.Success -> {
                                        Log.i("LoginFlow", "Login Value: ${login.value}")
                                        _state.update {
                                            Log.i(
                                                "LoginFlow",
                                                "Navigate to Signup: ${login.value && !registered.value}"
                                            )
                                            it.copy(
                                                loading = false,
                                                navigateToHome = login.value && registered.value,
                                                navigateToSignup = login.value && (!registered.value || user == null)
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
