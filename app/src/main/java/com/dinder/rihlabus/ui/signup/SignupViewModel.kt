package com.dinder.rihlabus.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _signupUiState = MutableStateFlow(SignupUiState())
    val signupUiState: Flow<SignupUiState> = _signupUiState

    init {
        viewModelScope.launch {
            repository.state.collect { repositoryState ->
                _signupUiState.update { state ->
                    Log.i(
                        "Verification",
                        "SignupViewModel init: logged=${repositoryState.isLoggedIn} registered=${repositoryState.isRegistered}"
                    )
                    state.copy(
                        isLoggedIn = repositoryState.isLoggedIn,
                        isRegistered = repositoryState.isRegistered
                    )
                }
            }
        }
    }

    fun signup(
        user: User
    ) {
        viewModelScope.launch {
            repository.register(user).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _signupUiState.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showErrorMessage(result.message)
                    }
                    is Result.Success -> {
                        _signupUiState.update { state ->
                            state.copy(loading = false, isRegistered = true)
                        }
                    }
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        _signupUiState.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, message)
            it.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _signupUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}