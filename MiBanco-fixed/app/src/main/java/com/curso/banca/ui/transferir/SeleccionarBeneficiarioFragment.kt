package com.curso.banca.ui.transferir

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.R
import com.curso.banca.databinding.FragmentSeleccionarBeneficiarioBinding
import com.curso.banca.ui.beneficiarios.AddBeneficiarioActivity
import com.curso.banca.ui.beneficiarios.BeneficiarioAdapter
import com.curso.banca.ui.beneficiarios.BeneficiarioMode
import com.curso.banca.ui.beneficiarios.SimpleTextWatcher
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SeleccionarBeneficiarioFragment : Fragment() {
    private var _binding: FragmentSeleccionarBeneficiarioBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransferenciaViewModel by activityViewModels()
    private lateinit var adapter: BeneficiarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSeleccionarBeneficiarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BeneficiarioAdapter(BeneficiarioMode.SELECT, onClick = {
            viewModel.seleccionarBeneficiario(it)
            findNavController().navigate(R.id.action_select_to_amount)
        })
        binding.rvBeneficiariosSelector.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiariosSelector.adapter = adapter
        binding.etSearch.addTextChangedListener(SimpleTextWatcher { adapter.filter(it) })
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.cardAddNuevo.setOnClickListener {
            startActivity(Intent(requireContext(), AddBeneficiarioActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.beneficiarios)
                state.mensajeError?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
