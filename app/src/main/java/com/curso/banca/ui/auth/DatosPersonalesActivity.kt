package com.curso.banca.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.databinding.ActivityDatosPersonalesBinding
import com.curso.banca.ui.main.MainActivity
import com.curso.banca.util.Validators
import com.curso.banca.util.toDisplayDate
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DatosPersonalesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatosPersonalesBinding
    private val viewModel: DatosPersonalesViewModel by viewModels()
    private var birthMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosPersonalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.etFechaNacimiento.editText?.setOnClickListener { openDatePicker() }
        binding.btnContinuar.setOnClickListener { submit() }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.btnContinuar.isEnabled = !state.isLoading
                if (state.success) openHome()
                state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun openDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha de nacimiento")
            .build()
        picker.addOnPositiveButtonClickListener {
            birthMillis = it
            binding.etFechaNacimiento.editText?.setText(it.toDisplayDate())
            binding.etFechaNacimiento.error = null
        }
        picker.show(supportFragmentManager, "birthDate")
    }

    private fun submit() {
        val nombre = binding.etNombre.editText?.text?.toString()?.trim().orEmpty()
        val apellidos = binding.etApellidos.editText?.text?.toString()?.trim().orEmpty()
        val celular = binding.etCelular.editText?.text?.toString()?.trim().orEmpty()
        listOf(binding.etNombre, binding.etApellidos, binding.etCelular, binding.etFechaNacimiento)
            .forEach { it.error = null }
        var valid = true
        if (nombre.isBlank()) {
            binding.etNombre.error = "Campo requerido"
            valid = false
        }
        if (apellidos.isBlank()) {
            binding.etApellidos.error = "Campo requerido"
            valid = false
        }
        if (!Validators.isValidPhone(celular)) {
            binding.etCelular.error = "Ingresa al menos 10 dígitos"
            valid = false
        }
        if (birthMillis == 0L || !Validators.isAdult(birthMillis)) {
            binding.etFechaNacimiento.error = "Debes ser mayor de 18 años"
            valid = false
        }
        if (valid) viewModel.saveProfile(nombre, apellidos, celular, birthMillis)
    }

    private fun openHome() {
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
