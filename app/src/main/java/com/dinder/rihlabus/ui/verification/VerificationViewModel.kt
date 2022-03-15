package com.dinder.rihlabus.ui.verification

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
import java.lang.Exception
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {
    init {
        viewModelScope.launch {
            repository.state.collect { repositoryState ->
                _verificationUiState.update {
                    it.copy(isLoggedIn = repositoryState.isLoggedIn)
                }
            }
        }
    }

    private val _verificationUiState = MutableStateFlow(VerificationUiState())
    val verificationUiState = _verificationUiState.asStateFlow()


    fun onNumberVerified(credential: AuthCredential) {
        _verificationUiState.update {
            it.copy(loading = true)
        }
        try {
            viewModelScope.launch {
                repository.login(credential)
            }
        } catch (exception: Exception) {
            _verificationUiState.update {
                val messages = it.messages + Message(
                    UUID.randomUUID().mostSignificantBits,
                    "Verification Error"
                )
                it.copy(messages = messages)
            }
        }
        _verificationUiState.update {
            it.copy(loading = false)
        }
    }

    fun onIncorrectCode() {
        _verificationUiState.update {
            val messages =
                it.messages + Message(id = UUID.randomUUID().mostSignificantBits, "Incorrect Code")
            it.copy(messages = messages)
        }
    }

    fun userMessageShown(messageId: Long) {
        _verificationUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}