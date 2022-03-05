package com.dinder.rihlabus.ui.login

import androidx.lifecycle.ViewModel
import com.dinder.rihlabus.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
}