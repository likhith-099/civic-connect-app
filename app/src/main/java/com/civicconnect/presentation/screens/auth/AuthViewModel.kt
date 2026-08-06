package com.civicconnect.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.data.dto.auth.LoginRequest
import com.civicconnect.data.dto.auth.RegisterRequest
import com.civicconnect.domain.usecase.auth.LoginUseCase
import com.civicconnect.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        if (!validateLogin(email, password)) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginUseCase(LoginRequest(email, password))
            result.onSuccess {
                _authState.value = AuthState.Success("Login Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Login Failed")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (!validateRegister(name, email, password)) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerUseCase(RegisterRequest(name, email, password))
            result.onSuccess {
                _authState.value = AuthState.Success("Registration Successful")
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Registration Failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun validateLogin(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                _authState.value = AuthState.Error("Email cannot be empty")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthState.Error("Invalid email format")
                false
            }
            password.isBlank() -> {
                _authState.value = AuthState.Error("Password cannot be empty")
                false
            }
            password.length < 6 -> {
                _authState.value = AuthState.Error("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun validateRegister(name: String, email: String, password: String): Boolean {
        return when {
            name.isBlank() -> {
                _authState.value = AuthState.Error("Name cannot be empty")
                false
            }
            email.isBlank() -> {
                _authState.value = AuthState.Error("Email cannot be empty")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _authState.value = AuthState.Error("Invalid email format")
                false
            }
            password.isBlank() -> {
                _authState.value = AuthState.Error("Password cannot be empty")
                false
            }
            password.length < 6 -> {
                _authState.value = AuthState.Error("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }
}
