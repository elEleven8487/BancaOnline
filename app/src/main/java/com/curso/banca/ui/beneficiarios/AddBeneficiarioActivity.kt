package com.curso.banca.ui.beneficiarios

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.data.model.Beneficiary
import com.curso.banca.data.model.BeneficiaryRequest
import com.curso.banca.databinding.ActivityAddBeneficiarioBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddBeneficiarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBeneficiarioBinding
    private val viewModel: AddBeneficiarioViewModel by viewModels()
    private var beneficiary: Beneficiary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBeneficiarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        beneficiary = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_BENEFICIARY, Beneficiary::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_BENEFICIARY)
        }
        fillIfEditing()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { submit() }
        binding.btnDelete.setOnClickListener { confirmDelete() }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.btnSave.isEnabled = !state.isLoading
                binding.btnDelete.isEnabled = !state.isLoading
                if (state.done) finish()
                state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun fillIfEditing() {
        val item = beneficiary ?: return
        binding.tvToolbarTitle.text = "Editar beneficiario"
        binding.etName.editText?.setText(item.name)
        binding.etLastName.editText?.setText(item.lastName)
        binding.etAccountNumber.editText?.setText(item.accountNumber)
        binding.etAlias.editText?.setText(item.alias)
        binding.btnDelete.visibility = View.VISIBLE
    }

    private fun submit() {
        val name = binding.etName.editText?.text?.toString()?.trim().orEmpty()
        val lastName = binding.etLastName.editText?.text?.toString()?.trim().orEmpty()
        val account = binding.etAccountNumber.editText?.text?.toString()?.trim().orEmpty()
        val alias = binding.etAlias.editText?.text?.toString()?.trim().orEmpty()
        listOf(binding.etName, binding.etLastName, binding.etAccountNumber, binding.etAlias).forEach { it.error = null }
        var valid = true
        if (name.isBlank()) { binding.etName.error = "Campo requerido"; valid = false }
        if (lastName.isBlank()) { binding.etLastName.error = "Campo requerido"; valid = false }
        if (account.length < 6) { binding.etAccountNumber.error = "Cuenta inválida"; valid = false }
        if (alias.isBlank()) { binding.etAlias.error = "Campo requerido"; valid = false }
        if (valid) {
            viewModel.save(beneficiary?.id, BeneficiaryRequest(name, lastName, account, alias))
        }
    }

    private fun confirmDelete() {
        val id = beneficiary?.id ?: return
        AlertDialog.Builder(this)
            .setTitle("Eliminar beneficiario")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.delete(id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        const val EXTRA_BENEFICIARY = "extra_beneficiary"
    }
}
