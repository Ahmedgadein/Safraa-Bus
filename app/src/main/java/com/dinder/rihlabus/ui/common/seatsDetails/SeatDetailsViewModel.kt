package com.dinder.rihlabus.ui.common.seatsDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.repository.trip.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SeatDetailsViewModel @Inject constructor(
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SeatDetailsUiState())
    val state = _state.asStateFlow()

    fun getTrip(id: Long) {
        viewModelScope.launch {
            repository.getTrip(id).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showErrorMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            loading = false,
                            seats = result.value.seats
                        )
                    }
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
