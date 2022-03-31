package com.dinder.rihlabus.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.domain.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val useCase: RegisterUserUseCase) : ViewModel() {
    private val _signupUiState = MutableStateFlow(SignupUiState())
    val signupUiState: Flow<SignupUiState> = _signupUiState

    fun signup(
        user: User,
        company: String,
        location: String
    ) {
        viewModelScope.launch {
            useCase(user, company, location).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _signupUiState.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showErrorMessage(result.message)
                    }
                    is Result.Success -> {
                        _signupUiState.update { state ->
                            state.copy(navigateToHome = result.value)
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
