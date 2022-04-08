package com.dinder.rihlabus.ui.tripDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.repository.trip.TripRepository
import com.dinder.rihlabus.utils.SeatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TripDetailsViewModel @Inject constructor(private val repository: TripRepository) :
    ViewModel() {
    private val _state = MutableStateFlow(TripDetailUiState())
    val state = _state.asStateFlow()

    fun getTrip(id: Long) {
        viewModelScope.launch {
            withContext(viewModelScope.coroutineContext) {
                repository.getTrip(id).collect { result ->
                    when (result) {
                        is Result.Loading -> _state.update { it.copy(loading = true) }
                        is Result.Error -> showErrorMessage(result.message)
                        is Result.Success -> _state.update {
                            it.copy(
                                loading = false,
                                trip = result.value
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateSeatState(tripId: Long, seatNumber: Int, state: SeatState) {
        viewModelScope.launch {
            repository.updateSeatState(tripId, seatNumber, state).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showErrorMessage(result.message)
                    is Result.Success -> getTrip(tripId)
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        _state.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, message)
            it.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == messageId }
            currentUiState.copy(messages = messages)
        }
    }
}
