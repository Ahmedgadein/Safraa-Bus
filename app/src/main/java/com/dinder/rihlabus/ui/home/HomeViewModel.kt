package com.dinder.rihlabus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.domain.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val useCase: GetUserUseCase) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init {
        checkUserVerification()
    }

    private fun checkUserVerification() {
        viewModelScope.launch {
            useCase.invoke().collect { result ->
                when (result) {
                    Result.Loading -> Unit
                    is Result.Error -> Unit
                    is Result.Success -> {
                        val verified = result.value.verified
                        _state.update {
                            it.copy(
                                navigateToUserVerificationScreen = !verified
                            )
                        }
                    }
                }
            }
        }
    }
}
