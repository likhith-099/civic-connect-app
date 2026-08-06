package com.civicconnect.presentation.screens.admin.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.domain.usecase.admin.AdminLoginUseCase
import com.civicconnect.presentation.screens.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val adminLoginUseCase: com.civicconnect.domain.usecase.admin.AdminLoginUseCase,
    private val adminRegisterUseCase: com.civicconnect.domain.usecase.admin.AdminRegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = adminLoginUseCase(com.civicconnect.data.dto.auth.LoginRequest(email, password))
            result.onSuccess {
                _authState.value = AuthState.Success("Admin Login Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Admin Login Failed")
            }
        }
    }

    fun register(name: String, email: String, password: String, municipalOffice: String, region: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || municipalOffice.isBlank() || region.isBlank()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = adminRegisterUseCase(
                com.civicconnect.data.dto.auth.AdminRegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    municipalOffice = municipalOffice,
                    region = region
                )
            )
            result.onSuccess {
                _authState.value = AuthState.Success("Admin Registered Successfully")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Admin Registration Failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
