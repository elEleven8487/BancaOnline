package com.curso.banca.ui.beneficiarios

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.databinding.FragmentBeneficiariosBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BeneficiariosFragment : Fragment() {
    private var _binding: FragmentBeneficiariosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BeneficiariosViewModel by viewModels()
    private lateinit var adapter: BeneficiarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBeneficiariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BeneficiarioAdapter(
            mode = BeneficiarioMode.CRUD,
            onClick = { openForm(it) },
            onMore = { anchor, item -> showMenu(anchor, item) }
        )
        binding.rvBeneficiarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeneficiarios.adapter = adapter
        binding.btnAddBeneficiario.setOnClickListener { openForm(null) }
        binding.btnAgregarPrimero.setOnClickListener { openForm(null) }
        binding.etSearch.addTextChangedListener(SimpleTextWatcher { adapter.filter(it) })

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.items)
                binding.rvBeneficiarios.visibility = if (state.items.isEmpty()) View.GONE else View.VISIBLE
                binding.layoutEmptyBeneficiarios.visibility = if (state.items.isEmpty()) View.VISIBLE else View.GONE
                state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun showMenu(anchor: View, item: Beneficiary) {
        PopupMenu(requireContext(), anchor).apply {
            menu.add("Editar")
            menu.add("Eliminar")
            setOnMenuItemClickListener { selected: MenuItem ->
                openForm(item)
                true
            }
            show()
        }
    }

    private fun openForm(item: Beneficiary?) {
        val intent = Intent(requireContext(), AddBeneficiarioActivity::class.java)
        if (item != null) intent.putExtra(AddBeneficiarioActivity.EXTRA_BENEFICIARY, item)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
