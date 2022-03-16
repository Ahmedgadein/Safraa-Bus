package com.dinder.rihlabus.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.data.model.Company
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
        credential: AuthCredential, name: String, phoneNumber: String,
        company: Company
    ) {
        _signupUiState.update {
            it.copy(loading = true)
        }

        try {
            viewModelScope.launch {
                repository.register(credential, name, phoneNumber, company)
            }
        } catch (exception: FirebaseException) {
            _signupUiState.update {
                val messages = it.messages + Message(
                    id = UUID.randomUUID().mostSignificantBits,
                    "Signup Failed, please try again"
                )
                it.copy(messages = messages)
            }
        } finally {
            _signupUiState.update {
                it.copy(loading = false)
            }
        }
    }

    fun userMessageShown(messageId: Long) {
        _signupUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}