package com.curso.banca.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.UserProfile
import com.curso.banca.data.network.ApiException
import com.curso.banca.data.repository.AuthRepository
import com.curso.banca.data.repository.BankRepository
import com.curso.banca.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DatosPersonalesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun saveProfile(nombre: String, apellidos: String, celular: String, fechaNacimiento: Long) {
        val user = AuthRepository.currentUser()
        if (user == null) {
            _uiState.value = AuthUiState(error = "No hay usuario autenticado.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            val profile = UserProfile(
                uid = user.uid,
                email = user.email.orEmpty(),
                nombre = nombre,
                apellidos = apellidos,
                celular = celular,
                fechaNacimiento = fechaNacimiento
            )

            ProfileRepository.saveProfile(profile)

            val accountResult = BankRepository.createAccountIfNeeded()
            val accountError = accountResult.exceptionOrNull()
            if (accountResult.isSuccess || (accountError is ApiException && accountError.errorCode == "account_exists")) {
                _uiState.value = AuthUiState(success = true)
            } else {
                _uiState.value = AuthUiState(error = accountError?.message ?: "No se pudo crear la cuenta bancaria.")
            }
        }
    }
}
