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
            repository.state.collect { repositoryState ->
                _loginUiState.update {
                    it.copy(
                        isRegistered = repositoryState.isRegistered,
                        isLoggedIn = repositoryState.isLoggedIn
                    )
                }
            }
        }
    }

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun login(credential: AuthCredential) {
        _loginUiState.update {
            it.copy(loading = true)
        }
        try {
            viewModelScope.launch {
                repository.login(credential)
            }
        } catch (exception: Exception) {
            _loginUiState.update {
                val messages = it.messages + Message(
                    id = UUID.randomUUID().mostSignificantBits,
                    "Login Failed"
                )

                it.copy(messages = messages)
            }
        }
        _loginUiState.update {
            it.copy(loading = false)
        }

    }

    fun userMessageShown(messageId: Long) {
        _loginUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}