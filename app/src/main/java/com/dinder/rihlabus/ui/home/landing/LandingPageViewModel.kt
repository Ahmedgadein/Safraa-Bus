package com.dinder.rihlabus.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.remote.auth.AuthRepository
import com.dinder.rihlabus.data.remote.user.UserRepository
import com.dinder.rihlabus.domain.UpdateAppUseCase
import com.dinder.rihlabus.ui.home.landing.LandingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LandingPageViewModel @Inject constructor(
    private val updateAppUseCase: UpdateAppUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    private val _state = MutableStateFlow(LandingUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.user.collect { user ->
                authRepository.isLoggedIn().collect { loggedIn ->
                    when (loggedIn) {
                        is Result.Loading -> Unit
                        is Result.Error -> Unit
                        is Result.Success -> {
                            updateAppUseCase.invoke().collect { shouldUpdate ->
                                when (shouldUpdate) {
                                    Result.Loading -> Unit
                                    is Result.Error -> showUserMessage(shouldUpdate.message)
                                    is Result.Success -> {
                                        _state.update { state ->
                                            state.copy(
                                                navigateToUpdateScreen = shouldUpdate.value,
                                                navigateToHome = loggedIn.value && user != null &&
                                                    !shouldUpdate.value,
                                                navigateToLogin = !loggedIn.value || user == null &&
                                                    !shouldUpdate.value
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
            it.copy(messages = messages)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
