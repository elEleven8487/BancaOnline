package com.curso.banca.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String) {
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            AuthRepository.register(email, password)
                .onSuccess { _uiState.value = AuthUiState(success = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "No se pudo crear la cuenta.") }
        }
    }
}
