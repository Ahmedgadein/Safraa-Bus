package com.dinder.rihlabus.ui.tripDetails

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.remote.trip.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    private val repository: TripRepository,
    private val resources: Resources
) :
    ViewModel() {
    private val _state = MutableStateFlow(TripDetailUiState())
    val state = _state.asStateFlow()

    fun getTrip(id: String) {
        observeTrip(id)
    }

    private fun observeTrip(id: String) {
        viewModelScope.launch {
            repository.observeTrip(id).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            trip = result.value,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    fun confirmPayment(tripId: String, seatNumber: Int) {
        viewModelScope.launch {
            repository.confirmPayment(tripId, seatNumber).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> showUserMessage(
                        resources.getString(R.string.payment_confirmed)
                    )
                }
            }
        }
    }

    fun disprovePayment(tripId: String, seatNumber: Int) {
        viewModelScope.launch {
            repository.disprovePayment(tripId, seatNumber).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> showUserMessage(
                        resources.getString(R.string.seat_reservation_cancelled)
                    )
                }
            }
        }
    }

    fun bookSeat(
        tripId: String,
        seatNumber: Int,
        passenger: String?
    ) {
        viewModelScope.launch {
            repository.bookSeat(tripId, seatNumber, passenger).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> showUserMessage(
                        resources.getString(R.string.seat_reserved)
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
