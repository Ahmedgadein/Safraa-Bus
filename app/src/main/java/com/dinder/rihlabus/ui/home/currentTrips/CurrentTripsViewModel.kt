package com.dinder.rihlabus.ui.home.currentTrips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.domain.CurrentTripsUseCase
import com.dinder.rihlabus.ui.home.TripsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CurrentTripsViewModel @Inject constructor(private val useCase: CurrentTripsUseCase) :
    ViewModel() {
    private val _state = MutableStateFlow(TripsUiState())
    val state: StateFlow<TripsUiState> = _state.asStateFlow()

    init {
        getTrips()
    }

    fun getTrips() = viewModelScope.launch {
        useCase().collect { result ->
            when (result) {
                Result.Loading -> _state.update { it.copy(loading = true) }
                is Result.Error -> showUserMessage(result.message)
                is Result.Success -> _state.update {
                    it.copy(
                        trips = result.value,
                        loading = false
                    )
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
