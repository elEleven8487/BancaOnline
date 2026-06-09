package com.curso.banca.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.databinding.ActivityLoginBinding
import com.curso.banca.ui.main.MainActivity
import com.curso.banca.util.Validators
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { submit() }
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener {
            Snackbar.make(binding.root, "Recuperación fuera de alcance del proyecto.", Snackbar.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.btnLogin.isEnabled = !state.isLoading
                if (state.success) openHome()
                state.error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun submit() {
        val email = binding.etEmail.editText?.text?.toString().orEmpty()
        val password = binding.etPassword.editText?.text?.toString().orEmpty()
        binding.etEmail.error = null
        binding.etPassword.error = null
        var valid = true
        if (!Validators.isValidEmail(email)) {
            binding.etEmail.error = "Correo inválido"
            valid = false
        }
        if (!Validators.isValidPassword(password)) {
            binding.etPassword.error = "Mínimo 8 caracteres"
            valid = false
        }
        if (valid) viewModel.login(email, password)
    }

    private fun openHome() {
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
