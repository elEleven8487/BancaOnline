package com.curso.banca.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.databinding.ActivityRegisterBinding
import com.curso.banca.util.Validators
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.tvGoToLogin.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { submit() }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.btnRegister.isEnabled = !state.isLoading
                if (state.success) {
                    startActivity(Intent(this@RegisterActivity, DatosPersonalesActivity::class.java))
                }
                state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun submit() {
        val email = binding.etEmail.editText?.text?.toString().orEmpty()
        val password = binding.etPassword.editText?.text?.toString().orEmpty()
        val confirm = binding.etConfirmPassword.editText?.text?.toString().orEmpty()
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
        var valid = true
        if (!Validators.isValidEmail(email)) {
            binding.etEmail.error = "Correo inválido"
            valid = false
        }
        if (!Validators.isValidPassword(password)) {
            binding.etPassword.error = "Mínimo 8 caracteres"
            valid = false
        }
        if (password != confirm) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            valid = false
        }
        if (valid) viewModel.register(email, password)
    }
}
