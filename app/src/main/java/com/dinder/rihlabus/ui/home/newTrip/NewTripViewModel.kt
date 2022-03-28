package com.dinder.rihlabus.ui.home.newTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.repository.trip.TripRepository
import com.dinder.rihlabus.domain.AddTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(private val useCase: AddTripUseCase) : ViewModel() {
    private val _newTripUiState = MutableStateFlow(NewTripUiState())
    val state = _newTripUiState.asStateFlow()

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            useCase(trip).collect {
                when (it) {
                    Result.Loading -> _newTripUiState.update { state ->
                        state.copy(loading = true)
                    }
                    is Result.Success -> _newTripUiState.update { state ->
                        state.copy(loading = false, isAdded = true)
                    }
                    is Result.Error -> showUserMessage(it.message)
                }
            }
        }
    }

    private fun showUserMessage(content: String) {
        _newTripUiState.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, content)
            it.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _newTripUiState.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}