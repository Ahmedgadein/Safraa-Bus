package com.dinder.rihlabus.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.remote.auth.AuthRepository
import com.dinder.rihlabus.data.remote.user.UserRepository
import com.dinder.rihlabus.ui.home.landing.LandingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    init {
        viewModelScope.launch {
            userRepository.user.collect { user ->
                authRepository.isLoggedIn().collect { loggedIn ->
                    when (loggedIn) {
                        is Result.Loading -> Unit
                        is Result.Error -> Unit
                        is Result.Success -> {
                            _state.update { state ->
                                state.copy(
                                    navigateToHome = loggedIn.value && user != null,
                                    navigateToLogin = !loggedIn.value || user == null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private val _state = MutableStateFlow(LandingUiState())
    val state = _state.asStateFlow()
}
