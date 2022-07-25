package com.dinder.rihlabus.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihlabus.common.Message
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.data.remote.user.UserRepository
import com.dinder.rihlabus.domain.GetCompaniesUseCase
import com.dinder.rihlabus.domain.GetDestinationsUseCase
import com.dinder.rihlabus.domain.GetUserUseCase
import com.dinder.rihlabus.domain.RegisterUserUseCase
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val registerUseCase: RegisterUserUseCase,
    private val destinationsUseCase: GetDestinationsUseCase,
    private val companiesUseCase: GetCompaniesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    @Inject
    lateinit var mixpanel: MixpanelAPI

    init {
        loadDestinations()
        loadCompanies()
    }

    fun signup(
        user: User
    ) {
        viewModelScope.launch {
            val company = _state.value.selectedCompany
            val location = _state.value.selectedLocation

            val props = JSONObject().apply {
                put("Name", user.name)
                put("Phone Number", user.phoneNumber)
                put("Company", company?.name)
                put("Location", location?.name)
            }
            mixpanel.track("Signup attempt", props)

            registerUseCase(user.copy(company = company, location = location)).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }
                    is Result.Error -> {
                        showUserMessage(result.message)
                    }
                    is Result.Success -> {
                        getUserUseCase().collect { user ->
                            when (user) {
                                Result.Loading -> Unit
                                is Result.Error -> showUserMessage(user.message)
                                is Result.Success -> {
                                    userRepository.add(user.value)
                                    _state.update { state ->
                                        state.copy(navigateToHome = result.value)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onCompanySelected(company: Company) {
        _state.update { it.copy(selectedCompany = company) }
    }

    fun onLocationSelected(location: Destination) {
        _state.update { it.copy(selectedLocation = location) }
    }

    private fun loadDestinations() {
        viewModelScope.launch {
            destinationsUseCase.invoke().collect { result ->
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
            companiesUseCase.invoke().collect { result ->
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
}
