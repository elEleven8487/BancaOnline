package com.curso.banca.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            AuthRepository.login(email, password)
                .onSuccess { _uiState.value = AuthUiState(success = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "No se pudo iniciar sesión.") }
        }
    }
}
