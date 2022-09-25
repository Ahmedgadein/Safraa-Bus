package com.dinder.rihlabus.ui.newTrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.data.remote.company.CompanyRepository
import com.dinder.rihlabus.domain.AddTripUseCase
import com.dinder.rihlabus.domain.GetDestinationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(
    private val addTripUseCase: AddTripUseCase,
    private val locationsUseCase: GetDestinationsUseCase,
    private val companyRepository: CompanyRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NewTripUiState())
    val state = _state.asStateFlow()

    init {
        loadLocations()
        loadCompanies()
    }

    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            addTripUseCase(
                trip.copy(
                    to = _state.value.to,
                    from = _state.value.from,
                    company = _state.value.company
                )
            ).collect {
                when (it) {
                    Result.Loading -> _state.update { state ->
                        state.copy(loading = true)
                    }
                    is Result.Success -> _state.update { state ->
                        state.copy(loading = false, isAdded = true)
                    }
                    is Result.Error -> showUserMessage(it.message)
                }
            }
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            locationsUseCase().collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            locations = result.value,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            companyRepository.getCompanies().collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            companies = result.value,
                            loading = false
                        )
                    }
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

    fun onFromLocationSelected(destination: Destination) {
        _state.update { it.copy(from = destination) }
    }

    fun onToLocationSelected(destination: Destination) {
        _state.update { it.copy(to = destination) }
    }

    fun onCompanySelected(company: Company) {
        _state.update { it.copy(company = company) }
    }
}
