package com.curso.banca.ui.beneficiarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.data.repository.BankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BeneficiariosUiState(
    val items: List<Beneficiary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class BeneficiariosViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BeneficiariosUiState(isLoading = true))
    val uiState: StateFlow<BeneficiariosUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            BankRepository.getBeneficiaries()
                .onSuccess { _uiState.value = BeneficiariosUiState(items = it) }
                .onFailure { _uiState.value = BeneficiariosUiState(error = it.message) }
        }
    }
}
