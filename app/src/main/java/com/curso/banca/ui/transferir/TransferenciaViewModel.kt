package com.curso.banca.ui.transferir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.data.repository.BankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransferenciaUiState(
    val beneficiarios: List<Beneficiary> = emptyList(),
    val beneficiarioSeleccionado: Beneficiary? = null,
    val saldoDisponible: Long = 0,
    val monto: Long = 0,
    val concepto: String = "",
    val montoValido: Boolean = false,
    val isLoading: Boolean = false,
    val transferenciaExitosa: Boolean = false,
    val mensajeError: String? = null
)

class TransferenciaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransferenciaUiState())
    val uiState: StateFlow<TransferenciaUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, mensajeError = null)
            val beneficiaries = BankRepository.getBeneficiaries()
            val account = BankRepository.getAccount()
            _uiState.value = _uiState.value.copy(
                beneficiarios = beneficiaries.getOrElse { emptyList() },
                saldoDisponible = account.getOrNull()?.balance ?: 0,
                isLoading = false,
                mensajeError = beneficiaries.exceptionOrNull()?.message ?: account.exceptionOrNull()?.message
            )
            validate()
        }
    }

    fun seleccionarBeneficiario(item: Beneficiary) {
        _uiState.value = _uiState.value.copy(beneficiarioSeleccionado = item)
    }

    fun actualizarMonto(value: Long) {
        _uiState.value = _uiState.value.copy(monto = value)
        validate()
    }

    fun actualizarConcepto(value: String) {
        _uiState.value = _uiState.value.copy(concepto = value)
    }

    fun ejecutarTransferencia() {
        val state = _uiState.value
        val beneficiary = state.beneficiarioSeleccionado ?: return
        if (!state.montoValido) return
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, mensajeError = null)
            BankRepository.createTransaction(beneficiary.id, state.monto, state.concepto.ifBlank { null })
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, transferenciaExitosa = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, mensajeError = it.message) }
        }
    }

    fun limpiarResultadoTransferencia() {
        _uiState.value = _uiState.value.copy(
            transferenciaExitosa = false,
            mensajeError = null,
            monto = 0,
            concepto = "",
            montoValido = false
        )
    }

    private fun validate() {
        val s = _uiState.value
        _uiState.value = s.copy(montoValido = s.monto > 0 && s.monto <= s.saldoDisponible)
    }
}
