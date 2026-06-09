package com.curso.banca.ui.transferir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.curso.banca.R
import com.curso.banca.databinding.FragmentMontoTransferenciaBinding
import com.curso.banca.ui.beneficiarios.SimpleTextWatcher
import com.curso.banca.util.maskAccount
import com.curso.banca.util.toCentsOrNull
import com.curso.banca.util.toCurrencyMXN
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MontoTransferenciaFragment : Fragment() {
    private var _binding: FragmentMontoTransferenciaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransferenciaViewModel by activityViewModels()
    private var transferHandled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMontoTransferenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        transferHandled = false
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.cardBeneficiarioSeleccionado.setOnClickListener { findNavController().popBackStack() }
        binding.btnCambiar.setOnClickListener { findNavController().popBackStack() }
        binding.etMonto.addTextChangedListener(SimpleTextWatcher {
            viewModel.actualizarMonto(it.toCentsOrNull() ?: 0)
        })
        binding.etConcepto.editText?.addTextChangedListener(SimpleTextWatcher {
            viewModel.actualizarConcepto(it)
        })
        for (i in 0 until binding.chipGroupSugeridos.childCount) {
            binding.chipGroupSugeridos.getChildAt(i).setOnClickListener { chip ->
                val amount = when ((chip as com.google.android.material.chip.Chip).text.toString()) {
                    "\$500" -> "500"
                    "\$1,000" -> "1000"
                    "\$1,500" -> "1500"
                    else -> "5000"
                }
                binding.etMonto.setText(amount)
                binding.etMonto.setSelection(binding.etMonto.text.length)
            }
        }
        binding.btnTransferir.setOnClickListener { confirmTransfer() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val beneficiary = state.beneficiarioSeleccionado
                binding.tvBeneficiario.text = beneficiary
                    ?.let { "${it.fullName}\n${it.alias} - ${it.accountNumber.maskAccount()}" }
                    .orEmpty()

                renderAmountState(state)

                if (state.transferenciaExitosa && !transferHandled) {
                    transferHandled = true
                    Toast.makeText(requireContext(), "Transferencia realizada", Toast.LENGTH_LONG).show()
                    viewModel.limpiarResultadoTransferencia()
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
                state.mensajeError?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun renderAmountState(state: TransferenciaUiState) {
        val amount = state.monto
        val exceeds = amount > state.saldoDisponible && amount > 0
        val valid = amount > 0 && !exceeds

        binding.btnTransferir.isEnabled = valid && !state.isLoading
        binding.btnTransferir.text = if (amount > 0) {
            "Transferir ${amount.toCurrencyMXN()}"
        } else {
            "Transferir"
        }

        binding.tvSaldoDisponible.compoundDrawablePadding = 8
        when {
            amount <= 0 -> {
                binding.tvSaldoDisponible.text = "Saldo disponible: ${state.saldoDisponible.toCurrencyMXN()} MXN"
                binding.tvSaldoDisponible.setBackgroundResource(R.drawable.bg_chip_neutral)
                binding.tvSaldoDisponible.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                binding.tvSaldoDisponible.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            exceeds -> {
                binding.tvSaldoDisponible.text = "El monto excede tu saldo"
                binding.tvSaldoDisponible.setBackgroundResource(R.drawable.bg_chip_danger)
                binding.tvSaldoDisponible.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger_text))
                binding.tvSaldoDisponible.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning, 0, 0, 0)
            }
            else -> {
                binding.tvSaldoDisponible.text = "Saldo disponible: ${state.saldoDisponible.toCurrencyMXN()} MXN"
                binding.tvSaldoDisponible.setBackgroundResource(R.drawable.bg_chip_success)
                binding.tvSaldoDisponible.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
                binding.tvSaldoDisponible.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0)
            }
        }
    }

    private fun confirmTransfer() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar transferencia")
            .setMessage("Deseas realizar esta transferencia?")
            .setPositiveButton("Transferir") { _, _ -> viewModel.ejecutarTransferencia() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
