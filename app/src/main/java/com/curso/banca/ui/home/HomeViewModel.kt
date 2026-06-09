package com.curso.banca.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.Account
import com.curso.banca.data.model.BankTransaction
import com.curso.banca.data.repository.BankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val account: Account? = null,
    val movimientos: List<BankTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val account = BankRepository.getAccount()
            val movements = BankRepository.getTransactions()
            _uiState.value = HomeUiState(
                account = account.getOrNull(),
                movimientos = movements.getOrElse { emptyList() },
                isLoading = false,
                message = account.exceptionOrNull()?.message ?: movements.exceptionOrNull()?.message
            )
        }
    }

    fun fund(amount: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            BankRepository.fundAccount(amount)
                .onSuccess { refresh() }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, message = it.message) }
        }
    }
}
