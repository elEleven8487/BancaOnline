package com.curso.banca.ui.beneficiarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.BeneficiaryRequest
import com.curso.banca.data.repository.BankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddBeneficiarioUiState(
    val isLoading: Boolean = false,
    val done: Boolean = false,
    val error: String? = null
)

class AddBeneficiarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddBeneficiarioUiState())
    val uiState: StateFlow<AddBeneficiarioUiState> = _uiState.asStateFlow()

    fun save(id: String?, request: BeneficiaryRequest) {
        _uiState.value = AddBeneficiarioUiState(isLoading = true)
        viewModelScope.launch {
            val result = if (id.isNullOrBlank()) {
                BankRepository.createBeneficiary(request)
            } else {
                BankRepository.updateBeneficiary(id, request)
            }
            result
                .onSuccess { _uiState.value = AddBeneficiarioUiState(done = true) }
                .onFailure { _uiState.value = AddBeneficiarioUiState(error = it.message) }
        }
    }

    fun delete(id: String) {
        _uiState.value = AddBeneficiarioUiState(isLoading = true)
        viewModelScope.launch {
            BankRepository.deleteBeneficiary(id)
                .onSuccess { _uiState.value = AddBeneficiarioUiState(done = true) }
                .onFailure { _uiState.value = AddBeneficiarioUiState(error = it.message) }
        }
    }
}
