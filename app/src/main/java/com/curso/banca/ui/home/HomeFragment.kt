package com.curso.banca.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.R
import com.curso.banca.data.repository.AuthRepository
import com.curso.banca.data.repository.ProfileRepository
import com.curso.banca.databinding.FragmentHomeBinding
import com.curso.banca.util.maskAccount
import com.curso.banca.util.toCentsOrNull
import com.curso.banca.util.toCurrencyMXN
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = MovimientoAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvMovimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovimientos.adapter = adapter
        binding.btnTransferir.setOnClickListener {
            findNavController().navigate(R.id.seleccionarBeneficiarioFragment)
        }
        binding.btnFondear.setOnClickListener { showFundDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val account = state.account
                binding.tvSaldoMonto.text = account?.balance?.toCurrencyMXN() ?: "\$0.00"
                binding.tvCuenta.text = account?.accountNumber?.let { "MXN · Cuenta ${it.maskAccount()}" }.orEmpty()
                adapter.submitList(state.movimientos)
                binding.rvMovimientos.visibility = if (state.movimientos.isEmpty()) View.GONE else View.VISIBLE
                binding.layoutEmptyMovimientos.visibility = if (state.movimientos.isEmpty()) View.VISIBLE else View.GONE
                state.message?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
        observeProfile()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun observeProfile() {
        val uid = AuthRepository.currentUser()?.uid ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            ProfileRepository.observeProfile(uid).collect { profile ->
                val name = profile?.nombreCompleto?.takeIf { it.isNotBlank() } ?: "Usuario"
                binding.tvUserName.text = name
                binding.imgAvatar.text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "M"
            }
        }
    }

    private fun showFundDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Monto en pesos"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(48, 16, 48, 16)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Fondear cuenta")
            .setMessage("Ingresa dinero ficticio para tu cuenta.")
            .setView(input)
            .setPositiveButton("Fondear") { _, _ ->
                val amount = input.text.toString().toCentsOrNull()
                if (amount != null && amount > 0) viewModel.fund(amount)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
