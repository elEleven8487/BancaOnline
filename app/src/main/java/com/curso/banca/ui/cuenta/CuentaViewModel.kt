package com.curso.banca.ui.cuenta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.UserProfile
import com.curso.banca.data.repository.AuthRepository
import com.curso.banca.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CuentaUiState(
    val profile: UserProfile? = null,
    val error: String? = null
)

class CuentaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CuentaUiState())
    val uiState: StateFlow<CuentaUiState> = _uiState.asStateFlow()

    fun observe() {
        val uid = AuthRepository.currentUser()?.uid ?: return
        viewModelScope.launch {
            ProfileRepository.observeProfile(uid).collect {
                _uiState.value = CuentaUiState(profile = it)
            }
        }
    }

    fun updatePhoto(uri: String) {
        val uid = AuthRepository.currentUser()?.uid ?: return
        viewModelScope.launch {
            ProfileRepository.updatePhoto(uid, uri)
        }
    }
}
