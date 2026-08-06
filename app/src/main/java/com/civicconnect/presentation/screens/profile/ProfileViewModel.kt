package com.civicconnect.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicconnect.domain.model.User
import com.civicconnect.domain.repository.AuthRepository
import com.civicconnect.domain.usecase.auth.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = authRepository.getCurrentUser()
            result.onSuccess { user ->
                _state.value = _state.value.copy(isLoading = false, user = user)
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun updateProfile(name: String, email: String, municipalArea: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = updateProfileUseCase(name, email, municipalArea)
            result.onSuccess { user ->
                _state.value = _state.value.copy(isLoading = false, user = user, isUpdateSuccess = true)
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false, error = it.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearFlags() {
        _state.value = _state.value.copy(isUpdateSuccess = false, error = null)
    }
}
