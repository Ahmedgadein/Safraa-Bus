package com.dinder.rihlabus.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class Message(val id: Long, val content: String)

data class LoginUiState(
    val isRegistered: Boolean = false,
    val isLoggedIn: Boolean = false,
    val messages: List<Message> = listOf()
)

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    init {
        viewModelScope.launch {
            _loginUiState.update {
                it.copy(isRegistered = repository.isRegistered)
            }
        }
    }
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun login(credential: AuthCredential) {
        viewModelScope.launch {
            when (repository.login(credential)) {
                is Result.Success -> {
                    _loginUiState.update {
                        it.copy(isLoggedIn = true)
                    }
                }
                is Result.Error -> {
                    _loginUiState.update {
                        val messages =
                            it.messages + Message(
                                id = UUID.randomUUID().mostSignificantBits,
                                content = "Failed to log in"
                            )
                        it.copy(messages = messages)
                    }
                }
            }
        }
    }

    fun userMessageShown(messageId: Long) {
        _loginUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}