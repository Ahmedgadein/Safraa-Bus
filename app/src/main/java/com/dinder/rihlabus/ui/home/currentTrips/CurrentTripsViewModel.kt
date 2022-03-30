package com.dinder.rihlabus.ui.home.currentTrips

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.domain.CurrentTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CurrentTripsViewModel @Inject constructor(private val useCase: CurrentTripsUseCase) :
    ViewModel() {
    private val _state = MutableStateFlow(CurrentTripsUiState())
    val state: StateFlow<CurrentTripsUiState> = _state.asStateFlow()

    init {
        Log.i("CurrentTrips", "Init ViewModel")
        viewModelScope.launch {
            getTrips()
        }
    }

    suspend fun getTrips() = viewModelScope.launch {
        useCase().collect { result ->
            when (result) {
                Result.Loading -> _state.update { it.copy(loading = true) }
                is Result.Error -> showUserMessage(result.message)
                is Result.Success -> _state.update {
                    Log.i("CurrentTrips", "ViewModel value: ${result.value.size}")
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
        _state.update {
            val messages = it.messages.filterNot { it.id == messageId }
            it.copy(messages = messages)
        }
    }
}
